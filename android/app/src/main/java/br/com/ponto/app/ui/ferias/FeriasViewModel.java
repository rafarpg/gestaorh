package br.com.ponto.app.ui.ferias;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.ponto.app.api.ApiClient;
import br.com.ponto.app.api.Chamada;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.model.Ferias;

public class FeriasViewModel extends ViewModel {

    private final MutableLiveData<Resultado<List<Ferias>>> lista = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Ferias>> alteracao = new MutableLiveData<>();
    private boolean pendentes;

    public LiveData<Resultado<List<Ferias>>> getLista() {
        return lista;
    }

    public LiveData<Resultado<Ferias>> getAlteracao() {
        return alteracao;
    }

    public void setPendentes(boolean pendentes) {
        this.pendentes = pendentes;
    }

    public void carregar() {
        Chamada.executar(pendentes ? ApiClient.api().feriasPendentes() : ApiClient.api().minhasFerias(), lista);
    }

    public void solicitar(String dataInicio, String dataFim) {
        Ferias ferias = new Ferias();
        ferias.dataInicio = dataInicio;
        ferias.dataFim = dataFim;
        Chamada.executar(ApiClient.api().solicitarFerias(ferias), alteracao);
    }

    public void decidir(Ferias ferias, String status) {
        Chamada.executar(ApiClient.api().decidirFerias(ferias.id, status), alteracao);
    }
}
