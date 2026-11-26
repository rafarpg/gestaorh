package br.com.ponto.app.ui.funcionarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import br.com.ponto.app.R;
import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.databinding.ActivityListaBinding;
import br.com.ponto.app.databinding.DialogFuncionarioBinding;
import br.com.ponto.app.databinding.ItemListaBinding;
import br.com.ponto.app.model.Funcionario;
import br.com.ponto.app.ui.ListaAdapter;
import br.com.ponto.app.ui.TelaComVoltar;
import br.com.ponto.app.ui.historico.HistoricoActivity;

public class FuncionariosActivity extends TelaComVoltar {

    private FuncionariosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityListaBinding tela = ActivityListaBinding.inflate(getLayoutInflater());
        setContentView(tela.getRoot());
        viewModel = new ViewModelProvider(this).get(FuncionariosViewModel.class);

        ListaAdapter<Funcionario> adapter = new ListaAdapter<>(this::preencher, this::abrirOpcoes);
        tela.lista.setAdapter(adapter);

        tela.botaoAdicionar.setVisibility(View.VISIBLE);
        tela.botaoAdicionar.setOnClickListener(v -> abrirFormulario(new Funcionario()));

        viewModel.getLista().observe(this, resultado -> {
            if (resultado.ok()) {
                tela.textoVazio.setVisibility(resultado.dados.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.atualizar(resultado.dados);
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.getSalvo().observe(this, evento -> {
            Resultado<Funcionario> resultado = evento.consumir();
            if (resultado == null) {
                return;
            }
            if (resultado.ok()) {
                Toast.makeText(this, R.string.funcionario_salvo, Toast.LENGTH_SHORT).show();
                viewModel.carregar();
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.getExcluido().observe(this, evento -> {
            Resultado<Void> resultado = evento.consumir();
            if (resultado == null) {
                return;
            }
            if (resultado.ok()) {
                Toast.makeText(this, R.string.funcionario_excluido, Toast.LENGTH_SHORT).show();
                viewModel.carregar();
            } else {
                tratarErro(resultado.erro);
            }
        });

        viewModel.carregar();
    }

    private void preencher(ItemListaBinding item, Funcionario funcionario) {
        item.textoTitulo.setText(funcionario.nome);
        item.textoDestaque.setText("RH".equals(funcionario.perfil) ? getString(R.string.rh) : "");
        item.textoLinha1.setText(funcionario.cargo + "  ·  " + funcionario.departamento);
        item.textoLinha2.setText(funcionario.email + "  ·  " + getString(R.string.cpf) + " " + funcionario.cpf);
    }

    private void abrirOpcoes(Funcionario funcionario) {
        String[] opcoes = {
                getString(R.string.ver_historico_ponto),
                getString(R.string.editar),
                getString(R.string.excluir)
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle(funcionario.nome)
                .setItems(opcoes, (dialogo, opcao) -> {
                    if (opcao == 0) {
                        startActivity(new Intent(this, HistoricoActivity.class)
                                .putExtra(HistoricoActivity.EXTRA_FUNCIONARIO_ID, funcionario.id));
                    } else if (opcao == 1) {
                        abrirFormulario(funcionario);
                    } else {
                        confirmarExclusao(funcionario);
                    }
                })
                .show();
    }

    private void confirmarExclusao(Funcionario funcionario) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.confirmar_exclusao_funcionario, funcionario.nome))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.excluir, (dialogo, botao) -> viewModel.excluir(funcionario))
                .show();
    }

    private void abrirFormulario(Funcionario funcionario) {
        DialogFuncionarioBinding formulario = DialogFuncionarioBinding.inflate(getLayoutInflater());
        formulario.campoNome.setText(funcionario.nome);
        formulario.campoCpf.setText(funcionario.cpf);
        formulario.campoEmail.setText(funcionario.email);
        formulario.campoCargo.setText(funcionario.cargo);
        formulario.campoDepartamento.setText(funcionario.departamento);
        formulario.checkRh.setChecked("RH".equals(funcionario.perfil));

        new MaterialAlertDialogBuilder(this)
                .setTitle(funcionario.id == null ? R.string.novo_funcionario : R.string.editar_funcionario)
                .setView(formulario.getRoot())
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.salvar, (dialogo, botao) -> {
                    Funcionario dados = new Funcionario();
                    dados.id = funcionario.id;
                    dados.nome = formulario.campoNome.getText().toString();
                    dados.cpf = formulario.campoCpf.getText().toString();
                    dados.email = formulario.campoEmail.getText().toString();
                    dados.cargo = formulario.campoCargo.getText().toString();
                    dados.departamento = formulario.campoDepartamento.getText().toString();
                    dados.perfil = formulario.checkRh.isChecked() ? "RH" : "FUNCIONARIO";
                    viewModel.salvar(dados);
                })
                .show();
    }
}
