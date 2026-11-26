package br.com.ponto.app.ui.historico;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.YearMonth;

import br.com.ponto.app.api.ApiClient;
import br.com.ponto.app.api.Chamada;
import br.com.ponto.app.api.Evento;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.model.DiaPonto;
import br.com.ponto.app.model.Historico;
import br.com.ponto.app.model.PontoManual;

public class HistoricoViewModel extends ViewModel {

    private final MutableLiveData<Resultado<Historico>> historico = new MutableLiveData<>();
    private final MutableLiveData<Evento<Resultado<DiaPonto>>> pontoManual = new MutableLiveData<>();
    private YearMonth mes = YearMonth.now();
    private Long funcionarioId;

    public LiveData<Resultado<Historico>> getHistorico() {
        return historico;
    }

    public LiveData<Evento<Resultado<DiaPonto>>> getPontoManual() {
        return pontoManual;
    }

    public YearMonth getMes() {
        return mes;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public void mudarMes(int quantidade) {
        mes = mes.plusMonths(quantidade);
        carregar();
    }

    public void carregar() {
        Chamada.executar(ApiClient.api().historico(mes.toString(), funcionarioId), historico);
    }

    public void salvarPontoManual(PontoManual ponto) {
        Chamada.executarEvento(ApiClient.api().lancarPontoManual(ponto), pontoManual);
    }
}
