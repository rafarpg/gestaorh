package br.com.ponto.app.api;

public class Evento<T> {

    private final T conteudo;
    private boolean consumido;

    public Evento(T conteudo) {
        this.conteudo = conteudo;
    }

    public T consumir() {
        if (consumido) {
            return null;
        }
        consumido = true;
        return conteudo;
    }
}
