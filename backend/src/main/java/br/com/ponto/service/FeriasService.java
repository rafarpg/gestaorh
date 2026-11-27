package br.com.ponto.service;

import br.com.ponto.dto.FeriasRequest;
import br.com.ponto.dto.FeriasResponse;
import br.com.ponto.model.Funcionario;
import br.com.ponto.model.SolicitacaoFerias;
import br.com.ponto.model.StatusFerias;
import br.com.ponto.repository.SolicitacaoFeriasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FeriasService {

    public static final long MAX_DIAS_FERIAS = 30;

    private final SolicitacaoFeriasRepository feriasRepository;

    public FeriasService(SolicitacaoFeriasRepository feriasRepository) {
        this.feriasRepository = feriasRepository;
    }

    public FeriasResponse solicitar(Funcionario funcionario, FeriasRequest request) {
        if (request.dataFim().isBefore(request.dataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim deve ser igual ou posterior à data de início");
        }
        long dias = ChronoUnit.DAYS.between(request.dataInicio(), request.dataFim()) + 1;
        if (dias > MAX_DIAS_FERIAS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O período de férias não pode ultrapassar " + MAX_DIAS_FERIAS + " dias");
        }
        SolicitacaoFerias solicitacao = new SolicitacaoFerias(funcionario, request.dataInicio(), request.dataFim());
        return FeriasResponse.de(feriasRepository.save(solicitacao));
    }

    public List<FeriasResponse> minhas(Funcionario funcionario) {
        return feriasRepository.findByFuncionarioOrderByDataInicioDesc(funcionario).stream()
                .map(FeriasResponse::de).toList();
    }

    public List<FeriasResponse> pendentes(Funcionario avaliador) {
        return feriasRepository.findByStatusOrderByDataInicioAsc(StatusFerias.PENDENTE).stream()
                .filter(s -> !s.getFuncionario().getId().equals(avaliador.getId()))
                .map(FeriasResponse::de).toList();
    }

    public FeriasResponse decidir(Long id, StatusFerias status, Funcionario avaliador) {
        if (status == StatusFerias.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe APROVADA ou RECUSADA");
        }
        SolicitacaoFerias solicitacao = feriasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada"));
        if (solicitacao.getFuncionario().getId().equals(avaliador.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode aprovar ou recusar as próprias férias");
        }
        if (solicitacao.getStatus() != StatusFerias.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitação já foi decidida");
        }
        solicitacao.setStatus(status);
        return FeriasResponse.de(feriasRepository.save(solicitacao));
    }
}
