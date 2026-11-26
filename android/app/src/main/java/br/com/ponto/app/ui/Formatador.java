package br.com.ponto.app.ui;

import android.graphics.Color;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.ponto.app.model.Marcacao;

public final class Formatador {

    public static final String[] MOTIVOS = {"ESQUECIMENTO", "PROBLEMA_CAPTURA", "FALTA_INTERNET", "OUTROS"};
    public static final String[] MOTIVOS_TEXTO = {"Esquecimento", "Problema na captura do ponto", "Falta de internet", "Outros"};

    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy", BRASIL);
    private static final DateTimeFormatter DATA_COM_DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy (EEE)", BRASIL);
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", BRASIL);

    private static final Map<String, String> TEXTOS = Map.of(
            "ENTRADA", "Entrada",
            "SAIDA_ALMOCO", "Saída almoço",
            "RETORNO_ALMOCO", "Retorno almoço",
            "SAIDA", "Saída",
            "PENDENTE", "Pendente",
            "APROVADA", "Aprovada",
            "RECUSADA", "Recusada");

    private Formatador() {
    }

    public static String texto(String codigo) {
        return TEXTOS.getOrDefault(codigo, codigo);
    }

    public static String motivo(String codigo) {
        for (int i = 0; i < MOTIVOS.length; i++) {
            if (MOTIVOS[i].equals(codigo)) {
                return MOTIVOS_TEXTO[i];
            }
        }
        return codigo;
    }

    public static String saldo(int minutos) {
        String sinal = minutos < 0 ? "-" : "+";
        int absoluto = Math.abs(minutos);
        return String.format(BRASIL, "%s%02d:%02d", sinal, absoluto / 60, absoluto % 60);
    }

    public static int corDoSaldo(int minutos) {
        return minutos < 0 ? Color.rgb(198, 40, 40) : Color.rgb(46, 125, 50);
    }

    public static String horas(int minutos) {
        return String.format(BRASIL, "%dh%02d", minutos / 60, minutos % 60);
    }

    public static String data(String iso) {
        return LocalDate.parse(iso).format(DATA);
    }

    public static String dataComDiaDaSemana(String iso) {
        return LocalDate.parse(iso).format(DATA_COM_DIA);
    }

    public static String mes(YearMonth mes) {
        String texto = mes.format(MES);
        return texto.substring(0, 1).toUpperCase(BRASIL) + texto.substring(1);
    }

    public static String horarios(List<Marcacao> marcacoes) {
        StringBuilder texto = new StringBuilder();
        for (Marcacao marcacao : marcacoes) {
            if (texto.length() > 0) {
                texto.append("  ·  ");
            }
            texto.append(marcacao.hora);
        }
        return texto.toString();
    }
}
