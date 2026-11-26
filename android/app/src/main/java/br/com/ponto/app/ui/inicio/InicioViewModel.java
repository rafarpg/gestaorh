package br.com.ponto.app.ui.inicio;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.ponto.app.api.ApiClient;
import br.com.ponto.app.api.Chamada;
import br.com.ponto.app.api.Evento;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.api.Sessao;
import br.com.ponto.app.model.Marcacao;
import br.com.ponto.app.model.PontoHoje;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioViewModel extends ViewModel {

    private final MutableLiveData<Resultado<PontoHoje>> hoje = new MutableLiveData<>();
    private final MutableLiveData<Evento<Resultado<Marcacao>>> marcacao = new MutableLiveData<>();

    public LiveData<Resultado<PontoHoje>> getHoje() {
        return hoje;
    }

    public LiveData<Evento<Resultado<Marcacao>>> getMarcacao() {
        return marcacao;
    }

    public void carregar() {
        Chamada.executar(ApiClient.api().pontoHoje(), hoje);
    }

    public void baterPonto() {
        Chamada.executarEvento(ApiClient.api().baterPonto(), marcacao);
    }

    public void sair() {
        String token = Sessao.token();
        Sessao.sair();
        if (token != null) {
            ApiClient.api().logout("Bearer " + token).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                }
            });
        }
    }
}
