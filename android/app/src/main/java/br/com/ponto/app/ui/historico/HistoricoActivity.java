package br.com.ponto.app.ui.historico;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.ponto.app.R;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.databinding.ActivityHistoricoBinding;
import br.com.ponto.app.databinding.DialogPontoManualBinding;
import br.com.ponto.app.databinding.ItemListaBinding;
import br.com.ponto.app.model.DiaPonto;
import br.com.ponto.app.model.Historico;
import br.com.ponto.app.model.Marcacao;
import br.com.ponto.app.model.PontoManual;
import br.com.ponto.app.ui.Formatador;
import br.com.ponto.app.ui.ListaAdapter;
import br.com.ponto.app.ui.Seletores;
import br.com.ponto.app.ui.TelaComVoltar;

public class HistoricoActivity extends TelaComVoltar {

    public static final String EXTRA_FUNCIONARIO_ID = "funcionarioId";

    private ActivityHistoricoBinding tela;
    private HistoricoViewModel viewModel;
    private ListaAdapter<DiaPonto> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tela = ActivityHistoricoBinding.inflate(getLayoutInflater());
        setContentView(tela.getRoot());
        viewModel = new ViewModelProvider(this).get(HistoricoViewModel.class);

        boolean historicoProprio = !getIntent().hasExtra(EXTRA_FUNCIONARIO_ID);
        if (!historicoProprio) {
            viewModel.setFuncionarioId(getIntent().getLongExtra(EXTRA_FUNCIONARIO_ID, 0));
            tela.botaoManual.setVisibility(View.GONE);
        }

        adapter = new ListaAdapter<>(this::preencherDia, historicoProprio ? this::abrirPontoManual : null);
        tela.lista.setAdapter(adapter);

        tela.botaoMesAnterior.setOnClickListener(v -> viewModel.mudarMes(-1));
        tela.botaoProximoMes.setOnClickListener(v -> viewModel.mudarMes(1));
        tela.botaoManual.setOnClickListener(v -> abrirPontoManual(null));

        viewModel.getHistorico().observe(this, resultado -> {
            if (resultado.ok()) {
                mostrar(resultado.dados);
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.getPontoManual().observe(this, evento -> {
            Resultado<DiaPonto> resultado = evento.consumir();
            if (resultado == null) {
                return;
            }
            if (resultado.ok()) {
                Toast.makeText(this, R.string.ponto_manual_salvo, Toast.LENGTH_SHORT).show();
                viewModel.carregar();
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.carregar();
    }

    private void mostrar(Historico historico) {
        setTitle(getString(R.string.historico) + " - " + historico.funcionarioNome);
        tela.textoMes.setText(Formatador.mes(viewModel.getMes()));
        tela.textoResumo.setText(getString(R.string.resumo_historico,
                Formatador.saldo(historico.saldoPeriodoMinutos), Formatador.saldo(historico.bancoHorasMinutos)));
        tela.textoVazio.setVisibility(historico.dias.isEmpty() ? View.VISIBLE : View.GONE);
        List<DiaPonto> dias = new ArrayList<>(historico.dias);
        Collections.reverse(dias);
        adapter.atualizar(dias);
    }

    private void preencherDia(ItemListaBinding item, DiaPonto dia) {
        item.textoTitulo.setText(Formatador.dataComDiaDaSemana(dia.data));
        item.textoLinha1.setText(Formatador.horarios(dia.marcacoes));
        if (dia.saldoMinutos != null) {
            item.textoDestaque.setText(Formatador.saldo(dia.saldoMinutos));
            item.textoDestaque.setTextColor(Formatador.corDoSaldo(dia.saldoMinutos));
            String linha2 = getString(R.string.trabalhado, Formatador.horas(dia.horasTrabalhadasMinutos));
            if (dia.manual) {
                linha2 += "  ·  " + getString(R.string.manual) + " (" + Formatador.motivo(dia.motivo) + ")";
            }
            item.textoLinha2.setText(linha2);
        } else {
            item.textoDestaque.setText(R.string.incompleto);
            item.textoDestaque.setTextColor(Formatador.corDoSaldo(-1));
            item.textoLinha2.setText("");
        }
    }

    private void abrirPontoManual(DiaPonto dia) {
        DialogPontoManualBinding formulario = DialogPontoManualBinding.inflate(getLayoutInflater());
        Seletores.data(formulario.campoData);
        Seletores.hora(formulario.campoEntrada);
        Seletores.hora(formulario.campoSaidaAlmoco);
        Seletores.hora(formulario.campoRetornoAlmoco);
        Seletores.hora(formulario.campoSaida);
        formulario.seletorMotivo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Formatador.MOTIVOS_TEXTO));

        Seletores.definirData(formulario.campoData, dia != null ? dia.data : LocalDate.now().toString());
        if (dia != null && dia.marcacoes != null) {
            for (Marcacao marcacao : dia.marcacoes) {
                if ("ENTRADA".equals(marcacao.tipo)) {
                    Seletores.definirHora(formulario.campoEntrada, marcacao.hora);
                } else if ("SAIDA_ALMOCO".equals(marcacao.tipo)) {
                    Seletores.definirHora(formulario.campoSaidaAlmoco, marcacao.hora);
                } else if ("RETORNO_ALMOCO".equals(marcacao.tipo)) {
                    Seletores.definirHora(formulario.campoRetornoAlmoco, marcacao.hora);
                } else if ("SAIDA".equals(marcacao.tipo)) {
                    Seletores.definirHora(formulario.campoSaida, marcacao.hora);
                }
            }
        }
        if (dia != null && dia.motivo != null) {
            int indice = Arrays.asList(Formatador.MOTIVOS).indexOf(dia.motivo);
            if (indice >= 0) {
                formulario.seletorMotivo.setSelection(indice);
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.ponto_manual)
                .setView(formulario.getRoot())
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.salvar, (dialogo, botao) -> {
                    PontoManual ponto = new PontoManual();
                    ponto.data = Seletores.valor(formulario.campoData);
                    ponto.entrada = Seletores.valor(formulario.campoEntrada);
                    ponto.saidaAlmoco = Seletores.valor(formulario.campoSaidaAlmoco);
                    ponto.retornoAlmoco = Seletores.valor(formulario.campoRetornoAlmoco);
                    ponto.saida = Seletores.valor(formulario.campoSaida);
                    ponto.motivo = Formatador.MOTIVOS[formulario.seletorMotivo.getSelectedItemPosition()];
                    viewModel.salvarPontoManual(ponto);
                })
                .show();
    }
}
