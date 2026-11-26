package br.com.ponto.app.ui.funcionarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.ponto.app.api.ApiClient;
import br.com.ponto.app.api.Chamada;
import br.com.ponto.app.api.Evento;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.model.Funcionario;

public class FuncionariosViewModel extends ViewModel {

    private final MutableLiveData<Resultado<List<Funcionario>>> lista = new MutableLiveData<>();
    private final MutableLiveData<Evento<Resultado<Funcionario>>> salvo = new MutableLiveData<>();
    private final MutableLiveData<Evento<Resultado<Void>>> excluido = new MutableLiveData<>();

    public LiveData<Resultado<List<Funcionario>>> getLista() {
        return lista;
    }

    public LiveData<Evento<Resultado<Funcionario>>> getSalvo() {
        return salvo;
    }

    public LiveData<Evento<Resultado<Void>>> getExcluido() {
        return excluido;
    }

    public void carregar() {
        Chamada.executar(ApiClient.api().listarFuncionarios(), lista);
    }

    public void salvar(Funcionario funcionario) {
        if (funcionario.id == null) {
            Chamada.executarEvento(ApiClient.api().criarFuncionario(funcionario), salvo);
        } else {
            Chamada.executarEvento(ApiClient.api().atualizarFuncionario(funcionario.id, funcionario), salvo);
        }
    }

    public void excluir(Funcionario funcionario) {
        Chamada.executarEvento(ApiClient.api().excluirFuncionario(funcionario.id), excluido);
    }
}
