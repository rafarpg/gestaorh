package br.com.ponto.config;

import br.com.ponto.model.Funcionario;
import br.com.ponto.model.OrigemPonto;
import br.com.ponto.model.Perfil;
import br.com.ponto.model.RegistroPonto;
import br.com.ponto.model.TipoMarcacao;
import br.com.ponto.repository.FuncionarioRepository;
import br.com.ponto.repository.RegistroPontoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final FuncionarioRepository funcionarioRepository;
    private final RegistroPontoRepository registroPontoRepository;

    public DadosIniciais(FuncionarioRepository funcionarioRepository, RegistroPontoRepository registroPontoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.registroPontoRepository = registroPontoRepository;
    }

    @Override
    public void run(String... args) {
        if (funcionarioRepository.count() > 0) {
            return;
        }
        criarFuncionario("Administrador RH", "00000000000", "rh@empresa.com", "Analista de RH", "Recursos Humanos", Perfil.RH);
        Funcionario maria = criarFuncionario("Maria Silva", "11111111111", "maria@empresa.com", "Desenvolvedora",
                "Tecnologia", Perfil.FUNCIONARIO);

        int[] variacoesNaSaida = {30, -15, 0, 10};
        for (int diasAtras = 21; diasAtras >= 1; diasAtras--) {
            LocalDate dia = LocalDate.now().minusDays(diasAtras);
            if (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            LocalTime[] horas = {LocalTime.of(8, 0), LocalTime.of(12, 0), LocalTime.of(13, 0),
                    LocalTime.of(17, 0).plusMinutes(variacoesNaSaida[diasAtras % 4])};
            for (TipoMarcacao tipo : TipoMarcacao.values()) {
                registroPontoRepository.save(new RegistroPonto(maria, dia, horas[tipo.ordinal()], tipo, OrigemPonto.NORMAL, null));
            }
        }
    }

    private Funcionario criarFuncionario(String nome, String cpf, String email, String cargo, String departamento,
                                         Perfil perfil) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setCpf(cpf);
        funcionario.setEmail(email);
        funcionario.setCargo(cargo);
        funcionario.setDepartamento(departamento);
        funcionario.setPerfil(perfil);
        return funcionarioRepository.save(funcionario);
    }
}
