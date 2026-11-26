package br.com.ponto.app;

import android.app.Application;

import br.com.ponto.app.api.Sessao;

public class PontoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Sessao.iniciar(this);
    }
}
