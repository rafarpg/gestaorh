package br.com.ponto.app.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import br.com.ponto.app.R;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.api.Sessao;
import br.com.ponto.app.databinding.ActivityInicioBinding;
import br.com.ponto.app.model.Marcacao;
import br.com.ponto.app.model.PontoHoje;
import br.com.ponto.app.ui.Formatador;
import br.com.ponto.app.ui.ferias.FeriasActivity;
import br.com.ponto.app.ui.funcionarios.FuncionariosActivity;
import br.com.ponto.app.ui.historico.HistoricoActivity;
import br.com.ponto.app.ui.login.LoginActivity;

public class InicioActivity extends AppCompatActivity {

    private ActivityInicioBinding tela;
    private InicioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tela = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(tela.getRoot());
        viewModel = new ViewModelProvider(this).get(InicioViewModel.class);

        tela.textoOla.setText(getString(R.string.ola_usuario, Sessao.nome()));
        tela.areaRh.setVisibility(Sessao.isRh() ? View.VISIBLE : View.GONE);

        tela.botaoBaterPonto.setOnClickListener(v -> {
            tela.botaoBaterPonto.setEnabled(false);
            viewModel.baterPonto();
        });
        tela.botaoHistorico.setOnClickListener(v -> startActivity(new Intent(this, HistoricoActivity.class)));
        tela.botaoFerias.setOnClickListener(v -> startActivity(new Intent(this, FeriasActivity.class)));
        tela.botaoFuncionarios.setOnClickListener(v -> startActivity(new Intent(this, FuncionariosActivity.class)));
        tela.botaoFeriasPendentes.setOnClickListener(v -> startActivity(
                new Intent(this, FeriasActivity.class).putExtra(FeriasActivity.EXTRA_PENDENTES, true)));
        tela.botaoSair.setOnClickListener(v -> {
            viewModel.sair();
            voltarParaLogin();
        });

        viewModel.getHoje().observe(this, resultado -> {
            if (resultado.ok()) {
                mostrar(resultado.dados);
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.getMarcacao().observe(this, evento -> {
            Resultado<Marcacao> resultado = evento.consumir();
            if (resultado == null) {
                return;
            }
            if (resultado.ok()) {
                Marcacao marcacao = resultado.dados;
                Toast.makeText(this, getString(R.string.marcacao_registrada,
                        Formatador.texto(marcacao.tipo), marcacao.hora), Toast.LENGTH_SHORT).show();
                viewModel.carregar();
            } else {
                tela.botaoBaterPonto.setEnabled(true);
                tratarErro(resultado.erro);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.carregar();
    }

    private void tratarErro(String erro) {
        if (!Sessao.logado()) {
            voltarParaLogin();
        } else {
            Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
        }
    }

    private void voltarParaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrar(PontoHoje hoje) {
        tela.textoBanco.setText(Formatador.saldo(hoje.bancoHorasMinutos));
        tela.textoBanco.setTextColor(Formatador.corDoSaldo(hoje.bancoHorasMinutos));

        StringBuilder marcacoes = new StringBuilder();
        for (Marcacao marcacao : hoje.marcacoes) {
            marcacoes.append(Formatador.texto(marcacao.tipo)).append(": ").append(marcacao.hora).append('\n');
        }
        tela.textoMarcacoes.setText(marcacoes.length() > 0 ? marcacoes.toString().trim()
                : getString(R.string.nenhuma_marcacao_hoje));

        tela.botaoBaterPonto.setEnabled(hoje.proximaMarcacao != null);
        if (hoje.manual) {
            tela.textoProxima.setText(R.string.dia_lancado_manualmente);
        } else if (hoje.proximaMarcacao == null) {
            tela.textoProxima.setText(R.string.todas_marcacoes_feitas);
        } else {
            tela.textoProxima.setText(getString(R.string.proxima_marcacao, Formatador.texto(hoje.proximaMarcacao)));
        }
    }
}
