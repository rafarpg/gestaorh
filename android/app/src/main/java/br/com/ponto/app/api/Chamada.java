package br.com.ponto.app.api;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import br.com.ponto.app.model.Erro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Chamada {

    private Chamada() {
    }

    public static <T> void executar(Call<T> chamada, MutableLiveData<Resultado<T>> destino) {
        chamada.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> resposta) {
                if (resposta.isSuccessful()) {
                    destino.setValue(Resultado.sucesso(resposta.body()));
                } else {
                    if (resposta.code() == 401) {
                        Sessao.sair();
                    }
                    destino.setValue(Resultado.falha(lerMensagemDeErro(resposta)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable erro) {
                destino.setValue(Resultado.falha("Não foi possível conectar ao servidor"));
            }
        });
    }

    private static String lerMensagemDeErro(Response<?> resposta) {
        try {
            Erro erro = new Gson().fromJson(resposta.errorBody().string(), Erro.class);
            if (erro != null && erro.mensagem != null) {
                return erro.mensagem;
            }
        } catch (Exception ignorada) {
        }
        return "Erro " + resposta.code();
    }
}
