package br.com.ponto.controller;

import br.com.ponto.config.AutenticacaoInterceptor;
import br.com.ponto.dto.FuncionarioRequest;
import br.com.ponto.dto.FuncionarioResponse;
import br.com.ponto.model.Funcionario;
import br.com.ponto.service.AutenticacaoService;
import br.com.ponto.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final AutenticacaoService autenticacaoService;

    public FuncionarioController(FuncionarioService funcionarioService, AutenticacaoService autenticacaoService) {
        this.funcionarioService = funcionarioService;
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping
    public List<FuncionarioResponse> listar(@RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.exigirRh(usuario);
        return funcionarioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponse criar(@RequestBody @Valid FuncionarioRequest request,
                                     @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.exigirRh(usuario);
        return funcionarioService.salvar(new Funcionario(), request);
    }

    @PutMapping("/{id}")
    public FuncionarioResponse atualizar(@PathVariable Long id, @RequestBody @Valid FuncionarioRequest request,
                                         @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.exigirRh(usuario);
        return funcionarioService.salvar(funcionarioService.buscar(id), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id,
                        @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        autenticacaoService.exigirRh(usuario);
        funcionarioService.excluir(id, usuario);
    }
}
