package br.com.ponto.controller;

import br.com.ponto.config.AutenticacaoInterceptor;
import br.com.ponto.dto.DiaPontoResponse;
import br.com.ponto.dto.HistoricoResponse;
import br.com.ponto.dto.MarcacaoResponse;
import br.com.ponto.dto.PontoHojeResponse;
import br.com.ponto.dto.PontoManualRequest;
import br.com.ponto.model.Funcionario;
import br.com.ponto.service.AutenticacaoService;
import br.com.ponto.service.FuncionarioService;
import br.com.ponto.service.PontoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/ponto")
public class PontoController {

    private final PontoService pontoService;
    private final FuncionarioService funcionarioService;
    private final AutenticacaoService autenticacaoService;

    public PontoController(PontoService pontoService, FuncionarioService funcionarioService,
                           AutenticacaoService autenticacaoService) {
        this.pontoService = pontoService;
        this.funcionarioService = funcionarioService;
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/hoje")
    public PontoHojeResponse hoje(@RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        return pontoService.hoje(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarcacaoResponse registrar(@RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        return pontoService.registrar(usuario);
    }

    @PutMapping("/manual")
    public DiaPontoResponse lancarManual(@RequestBody @Valid PontoManualRequest request,
                                         @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        return pontoService.lancarManual(usuario, request);
    }

    @GetMapping("/historico")
    public HistoricoResponse historico(@RequestParam(required = false) YearMonth mes,
                                       @RequestParam(required = false) LocalDate data,
                                       @RequestParam(required = false) Long funcionarioId,
                                       @RequestAttribute(AutenticacaoInterceptor.USUARIO_LOGADO) Funcionario usuario) {
        Funcionario funcionario = usuario;
        if (funcionarioId != null) {
            autenticacaoService.exigirRh(usuario);
            funcionario = funcionarioService.buscar(funcionarioId);
        }
        if (data != null) {
            return pontoService.historico(funcionario, data, data);
        }
        YearMonth periodo = mes != null ? mes : YearMonth.now();
        return pontoService.historico(funcionario, periodo.atDay(1), periodo.atEndOfMonth());
    }
}
