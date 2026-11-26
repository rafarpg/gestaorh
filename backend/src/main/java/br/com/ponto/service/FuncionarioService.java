package br.com.ponto.service;

import br.com.ponto.dto.FuncionarioRequest;
import br.com.ponto.dto.FuncionarioResponse;
import br.com.ponto.model.Funcionario;
import br.com.ponto.repository.FuncionarioRepository;
import br.com.ponto.repository.RegistroPontoRepository;
import br.com.ponto.repository.SolicitacaoFeriasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final RegistroPontoRepository registroPontoRepository;
    private final SolicitacaoFeriasRepository feriasRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository,
                              RegistroPontoRepository registroPontoRepository,
                              SolicitacaoFeriasRepository feriasRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.registroPontoRepository = registroPontoRepository;
        this.feriasRepository = feriasRepository;
    }

    public List<FuncionarioResponse> listar() {
        return funcionarioRepository.findAllByOrderByNomeAsc().stream().map(FuncionarioResponse::de).toList();
    }

    public Funcionario buscar(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
    }

    public FuncionarioResponse salvar(Funcionario funcionario, FuncionarioRequest request) {
        funcionario.setNome(request.nome());
        funcionario.setCpf(request.cpf());
        funcionario.setEmail(request.email().trim().toLowerCase());
        funcionario.setCargo(request.cargo());
        funcionario.setDepartamento(request.departamento());
        funcionario.setPerfil(request.perfil());
        return FuncionarioResponse.de(funcionarioRepository.saveAndFlush(funcionario));
    }

    @Transactional
    public void excluir(Long id, Funcionario usuarioLogado) {
        if (usuarioLogado.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode excluir o próprio usuário");
        }
        Funcionario funcionario = buscar(id);
        registroPontoRepository.deleteByFuncionario(funcionario);
        feriasRepository.deleteByFuncionario(funcionario);
        funcionarioRepository.delete(funcionario);
    }
}
