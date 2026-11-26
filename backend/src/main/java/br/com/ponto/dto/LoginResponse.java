package br.com.ponto.dto;

import br.com.ponto.model.Perfil;

public record LoginResponse(String token, Perfil perfil, String nome) {
}
