package br.com.ponto.app.model;

public class LoginRequest {

    public final String login;
    public final String senha;

    public LoginRequest(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }
}
