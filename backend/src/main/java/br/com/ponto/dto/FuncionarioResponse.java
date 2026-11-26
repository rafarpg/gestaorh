package br.com.ponto.dto;

import br.com.ponto.model.Funcionario;
import br.com.ponto.model.Perfil;

public record FuncionarioResponse(Long id, String nome, String cpf, String email, String cargo,
                                  String departamento, Perfil perfil) {

    public static FuncionarioResponse de(Funcionario f) {
        return new FuncionarioResponse(f.getId(), f.getNome(), f.getCpf(), f.getEmail(), f.getCargo(),
                f.getDepartamento(), f.getPerfil());
    }
}
