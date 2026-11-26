package br.com.ponto.model;

public enum TipoMarcacao {
    ENTRADA,
    SAIDA_ALMOCO,
    RETORNO_ALMOCO,
    SAIDA;

    public TipoMarcacao proxima() {
        int indice = ordinal() + 1;
        return indice < values().length ? values()[indice] : null;
    }
}
