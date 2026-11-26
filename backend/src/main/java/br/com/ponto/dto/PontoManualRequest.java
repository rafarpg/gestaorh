package br.com.ponto.dto;

import br.com.ponto.model.MotivoManual;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record PontoManualRequest(
        @NotNull(message = "Informe a data") LocalDate data,
        @NotNull(message = "Informe o horario de entrada") LocalTime entrada,
        @NotNull(message = "Informe o horario de saida para o almoco") LocalTime saidaAlmoco,
        @NotNull(message = "Informe o horario de retorno do almoco") LocalTime retornoAlmoco,
        @NotNull(message = "Informe o horario de saida") LocalTime saida,
        @NotNull(message = "Informe o motivo") MotivoManual motivo) {
}
