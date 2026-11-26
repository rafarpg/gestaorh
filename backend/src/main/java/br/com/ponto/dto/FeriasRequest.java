package br.com.ponto.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FeriasRequest(
        @NotNull(message = "Informe a data de inicio") LocalDate dataInicio,
        @NotNull(message = "Informe a data de fim") LocalDate dataFim) {
}
