package br.com.ponto.service;

import br.com.ponto.dto.DiaPontoResponse;
import br.com.ponto.dto.HistoricoResponse;
import br.com.ponto.dto.MarcacaoResponse;
import br.com.ponto.dto.PontoHojeResponse;
import br.com.ponto.dto.PontoManualRequest;
import br.com.ponto.model.Funcionario;
import br.com.ponto.model.OrigemPonto;
import br.com.ponto.model.RegistroPonto;
import br.com.ponto.model.TipoMarcacao;
import br.com.ponto.repository.RegistroPontoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class PontoService {

    private final RegistroPontoRepository registroPontoRepository;

    public PontoService(RegistroPontoRepository registroPontoRepository) {
        this.registroPontoRepository = registroPontoRepository;
    }

    public PontoHojeResponse hoje(Funcionario funcionario) {
        List<RegistroPonto> marcacoes = marcacoesDoDia(funcionario, LocalDate.now());
        boolean manual = isManual(marcacoes);
        return new PontoHojeResponse(
                marcacoes.stream().map(MarcacaoResponse::de).toList(),
                manual ? null : proximaMarcacao(marcacoes),
                manual,
                bancoDeHoras(funcionario));
    }

    public MarcacaoResponse registrar(Funcionario funcionario) {
        LocalDate hoje = LocalDate.now();
        List<RegistroPonto> marcacoes = marcacoesDoDia(funcionario, hoje);
        TipoMarcacao proxima = proximaMarcacao(marcacoes);
        if (isManual(marcacoes) || proxima == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ponto de hoje já está completo");
        }
        LocalTime agora = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        RegistroPonto registro = new RegistroPonto(funcionario, hoje, agora, proxima, OrigemPonto.NORMAL, null);
        return MarcacaoResponse.de(registroPontoRepository.save(registro));
    }

    @Transactional
    public DiaPontoResponse lancarManual(Funcionario funcionario, PontoManualRequest request) {
        List<LocalTime> horarios = List.of(request.entrada(), request.saidaAlmoco(), request.retornoAlmoco(), request.saida());
        for (int i = 1; i < horarios.size(); i++) {
            if (!horarios.get(i).isAfter(horarios.get(i - 1))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os horários devem estar em ordem crescente");
            }
        }
        registroPontoRepository.deleteByFuncionarioAndData(funcionario, request.data());
        registroPontoRepository.flush();

        TipoMarcacao[] tipos = TipoMarcacao.values();
        List<RegistroPonto> novos = new ArrayList<>();
        for (int i = 0; i < tipos.length; i++) {
            novos.add(new RegistroPonto(funcionario, request.data(), horarios.get(i), tipos[i],
                    OrigemPonto.MANUAL, request.motivo()));
        }
        return resumoDoDia(request.data(), registroPontoRepository.saveAll(novos));
    }

    public HistoricoResponse historico(Funcionario funcionario, LocalDate inicio, LocalDate fim) {
        List<RegistroPonto> registros = registroPontoRepository
                .findByFuncionarioAndDataBetweenOrderByDataAscHoraAscIdAsc(funcionario, inicio, fim);
        List<DiaPontoResponse> dias = agruparPorDia(registros).entrySet().stream()
                .map(dia -> resumoDoDia(dia.getKey(), dia.getValue()))
                .toList();
        int saldoPeriodo = dias.stream().map(DiaPontoResponse::saldoMinutos)
                .filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        return new HistoricoResponse(funcionario.getNome(), dias, saldoPeriodo, bancoDeHoras(funcionario));
    }

    private int bancoDeHoras(Funcionario funcionario) {
        return agruparPorDia(registroPontoRepository.findByFuncionarioOrderByDataAscHoraAscIdAsc(funcionario)).values().stream()
                .map(CalculadoraHoras::saldoDoDia)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private DiaPontoResponse resumoDoDia(LocalDate data, List<RegistroPonto> marcacoes) {
        boolean manual = isManual(marcacoes);
        return new DiaPontoResponse(
                data,
                marcacoes.stream().map(MarcacaoResponse::de).toList(),
                manual,
                manual ? marcacoes.get(0).getMotivo() : null,
                CalculadoraHoras.minutosTrabalhados(marcacoes),
                CalculadoraHoras.saldoDoDia(marcacoes));
    }

    private List<RegistroPonto> marcacoesDoDia(Funcionario funcionario, LocalDate data) {
        return registroPontoRepository.findByFuncionarioAndDataOrderByHoraAscIdAsc(funcionario, data);
    }

    private static Map<LocalDate, List<RegistroPonto>> agruparPorDia(List<RegistroPonto> registros) {
        return registros.stream().collect(Collectors.groupingBy(RegistroPonto::getData, TreeMap::new, Collectors.toList()));
    }

    private static TipoMarcacao proximaMarcacao(List<RegistroPonto> marcacoes) {
        return marcacoes.isEmpty() ? TipoMarcacao.ENTRADA : marcacoes.get(marcacoes.size() - 1).getTipo().proxima();
    }

    private static boolean isManual(List<RegistroPonto> marcacoes) {
        return !marcacoes.isEmpty() && marcacoes.get(0).getOrigem() == OrigemPonto.MANUAL;
    }
}
