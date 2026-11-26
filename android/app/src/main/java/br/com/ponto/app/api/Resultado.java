package br.com.ponto.app.api;

public class Resultado<T> {

    public final T dados;
    public final String erro;

    private Resultado(T dados, String erro) {
        this.dados = dados;
        this.erro = erro;
    }

    public static <T> Resultado<T> sucesso(T dados) {
        return new Resultado<>(dados, null);
    }

    public static <T> Resultado<T> falha(String erro) {
        return new Resultado<>(null, erro);
    }

    public boolean ok() {
        return erro == null;
    }
}
