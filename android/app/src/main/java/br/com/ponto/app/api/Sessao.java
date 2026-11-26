package br.com.ponto.app.api;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.ponto.app.model.LoginResponse;

public final class Sessao {

    private static SharedPreferences preferencias;

    private Sessao() {
    }

    public static void iniciar(Context contexto) {
        preferencias = contexto.getSharedPreferences("sessao", Context.MODE_PRIVATE);
    }

    public static void salvar(LoginResponse login) {
        preferencias.edit()
                .putString("token", login.token)
                .putString("perfil", login.perfil)
                .putString("nome", login.nome)
                .apply();
    }

    public static String token() {
        return preferencias.getString("token", null);
    }

    public static String nome() {
        return preferencias.getString("nome", "");
    }

    public static boolean isRh() {
        return "RH".equals(preferencias.getString("perfil", null));
    }

    public static boolean logado() {
        return token() != null;
    }

    public static void sair() {
        preferencias.edit().clear().apply();
    }
}
