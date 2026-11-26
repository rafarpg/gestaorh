package br.com.ponto.controller;

import br.com.ponto.config.AutenticacaoInterceptor;
import br.com.ponto.dto.FeriasRequest;
import br.com.ponto.dto.FeriasResponse;
import br.com.ponto.model.Funcionario;
import br.com.ponto.model.StatusFerias;
import br.com.ponto.service.AutenticacaoService;
import br.com.ponto.service.FeriasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ferias")
public class FeriasController {

    private final FeriasService feriasService;
    private final AutenticacaoService autenticacaoService;

    public FeriasController(FeriasService feriasService, AutenticacaoService autenticacaoService) {
        this.feriasService = feriasService;
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/minhas")
    public List<FeriasResponse> minhas(@RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        return feriasService.minhas(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeriasResponse solicitar(@RequestBody @Valid FeriasRequest request,
                                    @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        return feriasService.solicitar(usuario, request);
    }

    @GetMapping("/pendentes")
    public List<FeriasResponse> pendentes(@RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.exigirRh(usuario);
        return feriasService.pendentes();
    }

    @PutMapping("/{id}/{status}")
    public FeriasResponse decidir(@PathVariable Long id, @PathVariable StatusFerias status,
                                  @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.exigirRh(usuario);
        return feriasService.decidir(id, status);
    }
}
