package br.com.ponto.app.ui.funcionarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import br.com.ponto.app.R;
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
                Toast.makeText(this, resultado.erro, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSalvo().observe(this, resultado -> {
            Toast.makeText(this, resultado.ok() ? "Funcionário salvo" : resultado.erro, Toast.LENGTH_LONG).show();
            viewModel.carregar();
        });

        viewModel.getExcluido().observe(this, resultado -> {
            Toast.makeText(this, resultado.ok() ? "Funcionário excluído" : resultado.erro, Toast.LENGTH_LONG).show();
            viewModel.carregar();
        });

        viewModel.carregar();
    }

    private void preencher(ItemListaBinding item, Funcionario funcionario) {
        item.textoTitulo.setText(funcionario.nome);
        item.textoDestaque.setText("RH".equals(funcionario.perfil) ? "RH" : "");
        item.textoLinha1.setText(funcionario.cargo + "  ·  " + funcionario.departamento);
        item.textoLinha2.setText(funcionario.email + "  ·  CPF " + funcionario.cpf);
    }

    private void abrirOpcoes(Funcionario funcionario) {
        String[] opcoes = {"Ver histórico de ponto", "Editar", "Excluir"};
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
                .setMessage("Excluir " + funcionario.nome + " e todos os seus registros?")
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton("Excluir", (dialogo, botao) -> viewModel.excluir(funcionario))
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
                .setTitle(funcionario.id == null ? "Novo funcionário" : "Editar funcionário")
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
