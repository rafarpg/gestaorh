package br.com.ponto.dto;

import br.com.ponto.model.SolicitacaoFerias;
import br.com.ponto.model.StatusFerias;

import java.time.LocalDate;

public record FeriasResponse(Long id, String funcionarioNome, LocalDate dataInicio, LocalDate dataFim,
                             StatusFerias status) {

    public static FeriasResponse de(SolicitacaoFerias s) {
        return new FeriasResponse(s.getId(), s.getFuncionario().getNome(), s.getDataInicio(), s.getDataFim(),
                s.getStatus());
    }
}
