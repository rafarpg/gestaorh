package br.com.ponto.service;

import br.com.ponto.dto.LoginRequest;
import br.com.ponto.dto.LoginResponse;
import br.com.ponto.model.Funcionario;
import br.com.ponto.model.Perfil;
import br.com.ponto.repository.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AutenticacaoService {

    private static final String SENHA_PADRAO = "senha123";

    private final FuncionarioRepository funcionarioRepository;

    public AutenticacaoService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Funcionario funcionario = funcionarioRepository.findByEmail(request.login().trim().toLowerCase())
                .filter(f -> SENHA_PADRAO.equals(request.senha()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login ou senha incorretos"));

        funcionario.setToken(UUID.randomUUID().toString());
        funcionarioRepository.save(funcionario);
        return new LoginResponse(funcionario.getToken(), funcionario.getPerfil(), funcionario.getNome());
    }

    public Funcionario buscarPorToken(String token) {
        return funcionarioRepository.findByToken(String.valueOf(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faca login para continuar"));
    }

    public void exigirRh(Funcionario funcionario) {
        if (funcionario.getPerfil() != Perfil.RH) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso permitido apenas ao RH");
        }
    }
}
