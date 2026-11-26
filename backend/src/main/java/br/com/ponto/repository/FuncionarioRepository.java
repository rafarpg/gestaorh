package br.com.ponto.repository;

import br.com.ponto.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findAllByOrderByNomeAsc();

    Optional<Funcionario> findByEmail(String email);

    Optional<Funcionario> findByToken(String token);
}
