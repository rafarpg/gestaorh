package br.com.ponto.repository;

import br.com.ponto.model.Funcionario;
import br.com.ponto.model.SolicitacaoFerias;
import br.com.ponto.model.StatusFerias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoFeriasRepository extends JpaRepository<SolicitacaoFerias, Long> {

    List<SolicitacaoFerias> findByFuncionarioOrderByDataInicioDesc(Funcionario funcionario);

    List<SolicitacaoFerias> findByStatusOrderByDataInicioAsc(StatusFerias status);

    void deleteByFuncionario(Funcionario funcionario);
}
