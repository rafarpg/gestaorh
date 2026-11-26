package br.com.ponto.dto;

import br.com.ponto.model.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FuncionarioRequest(
        @NotBlank(message = "Informe o nome") String nome,
        @NotBlank(message = "Informe o CPF") String cpf,
        @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail invalido") String email,
        @NotBlank(message = "Informe o cargo") String cargo,
        @NotBlank(message = "Informe o departamento") String departamento,
        @NotNull(message = "Informe o perfil") Perfil perfil) {
}
