package br.com.ponto.dto;

import br.com.ponto.model.MotivoManual;

import java.time.LocalDate;
import java.util.List;

public record DiaPontoResponse(
        LocalDate data,
        List<MarcacaoResponse> marcacoes,
        boolean manual,
        MotivoManual motivo,
        Integer horasTrabalhadasMinutos,
        Integer saldoMinutos) {
}
