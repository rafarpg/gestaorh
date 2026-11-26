package br.com.ponto.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import br.com.ponto.app.api.Resultado;
import br.com.ponto.app.api.Sessao;
import br.com.ponto.app.databinding.ActivityLoginBinding;
import br.com.ponto.app.model.LoginResponse;
import br.com.ponto.app.ui.inicio.InicioActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding tela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Sessao.logado()) {
            abrirInicio();
            return;
        }
        tela = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(tela.getRoot());

        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        tela.botaoEntrar.setOnClickListener(v -> {
            carregando(true);
            viewModel.entrar(tela.campoEmail.getText().toString(), tela.campoSenha.getText().toString());
        });

        viewModel.getResultado().observe(this, evento -> {
            Resultado<LoginResponse> resultado = evento.consumir();
            if (resultado == null) {
                return;
            }
            carregando(false);
            if (resultado.ok()) {
                Sessao.salvar(resultado.dados);
                abrirInicio();
            } else {
                Toast.makeText(this, resultado.erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void carregando(boolean ativo) {
        tela.carregando.setVisibility(ativo ? View.VISIBLE : View.GONE);
        tela.botaoEntrar.setEnabled(!ativo);
    }

    private void abrirInicio() {
        startActivity(new Intent(this, InicioActivity.class));
        finish();
    }
}
