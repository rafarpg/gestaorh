package br.com.ponto.dto;

import br.com.ponto.model.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record FuncionarioRequest(
        @NotBlank(message = "Informe o nome") String nome,
        @NotBlank(message = "Informe o CPF") @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos") String cpf,
        @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") String email,
        @NotBlank(message = "Informe o cargo") String cargo,
        @NotBlank(message = "Informe o departamento") String departamento,
        @NotNull(message = "Informe o perfil") Perfil perfil) {
}
