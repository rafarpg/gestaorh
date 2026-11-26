package br.com.ponto.app.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.ponto.app.api.ApiClient;
import br.com.ponto.app.api.Chamada;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.model.Marcacao;
import br.com.ponto.app.model.PontoHoje;

public class InicioViewModel extends ViewModel {

    private final MutableLiveData<Resultado<PontoHoje>> hoje = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Marcacao>> marcacao = new MutableLiveData<>();

    public LiveData<Resultado<PontoHoje>> getHoje() {
        return hoje;
    }

    public LiveData<Resultado<Marcacao>> getMarcacao() {
        return marcacao;
    }

    public void carregar() {
        Chamada.executar(ApiClient.api().pontoHoje(), hoje);
    }

    public void baterPonto() {
        Chamada.executar(ApiClient.api().baterPonto(), marcacao);
    }
}
