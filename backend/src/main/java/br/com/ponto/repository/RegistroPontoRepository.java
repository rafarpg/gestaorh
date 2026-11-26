package br.com.ponto.repository;

import br.com.ponto.model.Funcionario;
import br.com.ponto.model.RegistroPonto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, Long> {

    List<RegistroPonto> findByFuncionarioAndDataOrderByHoraAscIdAsc(Funcionario funcionario, LocalDate data);

    List<RegistroPonto> findByFuncionarioAndDataBetweenOrderByDataAscHoraAscIdAsc(Funcionario funcionario,
                                                                                   LocalDate inicio, LocalDate fim);

    List<RegistroPonto> findByFuncionarioOrderByDataAscHoraAscIdAsc(Funcionario funcionario);

    void deleteByFuncionarioAndData(Funcionario funcionario, LocalDate data);

    void deleteByFuncionario(Funcionario funcionario);
}
