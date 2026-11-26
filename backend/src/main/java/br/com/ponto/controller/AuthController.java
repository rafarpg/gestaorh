package br.com.ponto.controller;

import br.com.ponto.config.AutenticacaoInterceptor;
import br.com.ponto.dto.LoginRequest;
import br.com.ponto.dto.LoginResponse;
import br.com.ponto.model.Funcionario;
import br.com.ponto.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;

    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return autenticacaoService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.logout(usuario);
    }
}
