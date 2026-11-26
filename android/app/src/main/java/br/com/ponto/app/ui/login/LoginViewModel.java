package br.com.ponto.app.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.ponto.app.api.ApiClient;
import br.com.ponto.app.api.Chamada;
import br.com.ponto.app.api.Evento;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.model.LoginRequest;
import br.com.ponto.app.model.LoginResponse;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Evento<Resultado<LoginResponse>>> resultado = new MutableLiveData<>();

    public LiveData<Evento<Resultado<LoginResponse>>> getResultado() {
        return resultado;
    }

    public void entrar(String login, String senha) {
        Chamada.executarEvento(ApiClient.api().login(new LoginRequest(login, senha)), resultado);
    }
}
