package br.com.ponto.dto;

import br.com.ponto.model.MotivoManual;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record PontoManualRequest(
        @NotNull(message = "Informe a data") LocalDate data,
        @NotNull(message = "Informe o horário de entrada") LocalTime entrada,
        @NotNull(message = "Informe o horário de saída para o almoço") LocalTime saidaAlmoco,
        @NotNull(message = "Informe o horário de retorno do almoço") LocalTime retornoAlmoco,
        @NotNull(message = "Informe o horário de saída") LocalTime saida,
        @NotNull(message = "Informe o motivo") MotivoManual motivo) {
}
