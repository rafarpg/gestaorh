package br.com.ponto.app.ui.historico;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import br.com.ponto.app.R;
import br.com.ponto.app.databinding.ActivityHistoricoBinding;
import br.com.ponto.app.databinding.DialogPontoManualBinding;
import br.com.ponto.app.databinding.ItemListaBinding;
import br.com.ponto.app.model.DiaPonto;
import br.com.ponto.app.model.Historico;
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
                Toast.makeText(this, resultado.erro, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getPontoManual().observe(this, resultado -> {
            if (resultado.ok()) {
                Toast.makeText(this, "Ponto manual salvo", Toast.LENGTH_SHORT).show();
                viewModel.carregar();
            } else {
                Toast.makeText(this, resultado.erro, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.carregar();
    }

    private void mostrar(Historico historico) {
        setTitle(getString(R.string.historico) + " - " + historico.funcionarioNome);
        tela.textoMes.setText(Formatador.mes(viewModel.getMes()));
        tela.textoResumo.setText("Saldo do mês: " + Formatador.saldo(historico.saldoPeriodoMinutos)
                + "     Banco de horas: " + Formatador.saldo(historico.bancoHorasMinutos));
        tela.textoVazio.setVisibility(historico.dias.isEmpty() ? View.VISIBLE : View.GONE);
        Collections.reverse(historico.dias);
        adapter.atualizar(historico.dias);
    }

    private void preencherDia(ItemListaBinding item, DiaPonto dia) {
        item.textoTitulo.setText(Formatador.dataComDiaDaSemana(dia.data));
        item.textoLinha1.setText(Formatador.horarios(dia.marcacoes));
        if (dia.saldoMinutos != null) {
            item.textoDestaque.setText(Formatador.saldo(dia.saldoMinutos));
            item.textoDestaque.setTextColor(Formatador.corDoSaldo(dia.saldoMinutos));
            String linha2 = "Trabalhado: " + Formatador.horas(dia.horasTrabalhadasMinutos);
            if (dia.manual) {
                linha2 += "  ·  " + getString(R.string.manual) + " (" + Formatador.motivo(dia.motivo) + ")";
            }
            item.textoLinha2.setText(linha2);
        } else {
            item.textoDestaque.setText("Incompleto");
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
        if (dia != null && dia.marcacoes.size() == 4) {
            Seletores.definirHora(formulario.campoEntrada, dia.marcacoes.get(0).hora);
            Seletores.definirHora(formulario.campoSaidaAlmoco, dia.marcacoes.get(1).hora);
            Seletores.definirHora(formulario.campoRetornoAlmoco, dia.marcacoes.get(2).hora);
            Seletores.definirHora(formulario.campoSaida, dia.marcacoes.get(3).hora);
        }
        if (dia != null && dia.motivo != null) {
            formulario.seletorMotivo.setSelection(Arrays.asList(Formatador.MOTIVOS).indexOf(dia.motivo));
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
