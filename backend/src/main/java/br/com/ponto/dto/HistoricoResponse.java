package br.com.ponto.dto;

import java.util.List;

public record HistoricoResponse(
        String funcionarioNome,
        List<DiaPontoResponse> dias,
        int saldoPeriodoMinutos,
        int bancoHorasMinutos) {
}
