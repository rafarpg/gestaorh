package br.com.ponto.app.ui.ferias;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import br.com.ponto.app.R;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.databinding.ActivityListaBinding;
import br.com.ponto.app.databinding.DialogFeriasBinding;
import br.com.ponto.app.databinding.ItemListaBinding;
import br.com.ponto.app.model.Ferias;
import br.com.ponto.app.ui.Formatador;
import br.com.ponto.app.ui.ListaAdapter;
import br.com.ponto.app.ui.Seletores;
import br.com.ponto.app.ui.TelaComVoltar;

public class FeriasActivity extends TelaComVoltar {

    public static final String EXTRA_PENDENTES = "pendentes";

    private FeriasViewModel viewModel;
    private boolean pendentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityListaBinding tela = ActivityListaBinding.inflate(getLayoutInflater());
        setContentView(tela.getRoot());
        viewModel = new ViewModelProvider(this).get(FeriasViewModel.class);

        pendentes = getIntent().getBooleanExtra(EXTRA_PENDENTES, false);
        viewModel.setPendentes(pendentes);
        setTitle(pendentes ? R.string.ferias_pendentes : R.string.minhas_ferias);

        ListaAdapter<Ferias> adapter = new ListaAdapter<>(this::preencher, null);
        tela.lista.setAdapter(adapter);

        if (!pendentes) {
            tela.botaoAdicionar.setText(R.string.solicitar_ferias);
            tela.botaoAdicionar.setVisibility(View.VISIBLE);
            tela.botaoAdicionar.setOnClickListener(v -> abrirSolicitacao());
        }

        viewModel.getLista().observe(this, resultado -> {
            if (resultado.ok()) {
                tela.textoVazio.setVisibility(resultado.dados.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.atualizar(resultado.dados);
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.getAlteracao().observe(this, evento -> {
            Resultado<Ferias> resultado = evento.consumir();
            if (resultado == null) {
                return;
            }
            if (resultado.ok()) {
                String status = resultado.dados.status;
                String mensagem = "PENDENTE".equals(status) ? getString(R.string.solicitacao_enviada)
                        : getString(R.string.ferias_decididas, Formatador.texto(status).toLowerCase());
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
                viewModel.carregar();
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.carregar();
    }

    private void preencher(ItemListaBinding item, Ferias ferias) {
        long dias = ChronoUnit.DAYS.between(LocalDate.parse(ferias.dataInicio), LocalDate.parse(ferias.dataFim)) + 1;
        String periodo = Formatador.data(ferias.dataInicio) + " a " + Formatador.data(ferias.dataFim)
                + " (" + dias + (dias == 1 ? " dia)" : " dias)");
        item.textoTitulo.setText(pendentes ? ferias.funcionarioNome : periodo);
        item.textoLinha1.setText(pendentes ? periodo : "");
        item.textoDestaque.setText(Formatador.texto(ferias.status));
        item.areaBotoes.setVisibility(pendentes ? View.VISIBLE : View.GONE);
        item.botaoAprovar.setOnClickListener(v -> viewModel.decidir(ferias, "APROVADA"));
        item.botaoRecusar.setOnClickListener(v -> viewModel.decidir(ferias, "RECUSADA"));
    }

    private void abrirSolicitacao() {
        DialogFeriasBinding formulario = DialogFeriasBinding.inflate(getLayoutInflater());
        Seletores.data(formulario.campoInicio);
        Seletores.data(formulario.campoFim);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.solicitar_ferias)
                .setView(formulario.getRoot())
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.salvar, (dialogo, botao) -> viewModel.solicitar(
                        Seletores.valor(formulario.campoInicio), Seletores.valor(formulario.campoFim)))
                .show();
    }
}
