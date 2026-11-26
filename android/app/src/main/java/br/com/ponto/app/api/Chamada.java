package br.com.ponto.app.api;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.function.Consumer;

import br.com.ponto.app.model.Erro;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Chamada {

    private Chamada() {
    }

    public static <T> void executar(Call<T> chamada, MutableLiveData<Resultado<T>> destino) {
        executar(chamada, destino::setValue);
    }

    public static <T> void executarEvento(Call<T> chamada, MutableLiveData<Evento<Resultado<T>>> destino) {
        executar(chamada, resultado -> destino.setValue(new Evento<>(resultado)));
    }

    private static <T> void executar(Call<T> chamada, Consumer<Resultado<T>> destino) {
        chamada.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> resposta) {
                if (resposta.isSuccessful()) {
                    destino.accept(Resultado.sucesso(resposta.body()));
                } else {
                    if (resposta.code() == 401) {
                        Sessao.sair();
                    }
                    destino.accept(Resultado.falha(lerMensagemDeErro(resposta)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable erro) {
                destino.accept(Resultado.falha("Não foi possível conectar ao servidor"));
            }
        });
    }

    private static String lerMensagemDeErro(Response<?> resposta) {
        try (ResponseBody corpo = resposta.errorBody()) {
            if (corpo != null) {
                Erro erro = new Gson().fromJson(corpo.string(), Erro.class);
                if (erro != null && erro.mensagem != null) {
                    return erro.mensagem;
                }
            }
        } catch (Exception ignorada) {
        }
        return "Erro " + resposta.code();
    }
}
