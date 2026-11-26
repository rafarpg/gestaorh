package br.com.ponto.app.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.LocalTime;

public final class Seletores {

    private Seletores() {
    }

    public static void data(EditText campo) {
        campo.setOnClickListener(v -> {
            LocalDate atual = valor(campo) != null ? LocalDate.parse(valor(campo)) : LocalDate.now();
            new DatePickerDialog(campo.getContext(),
                    (seletor, ano, mes, dia) -> definirData(campo, LocalDate.of(ano, mes + 1, dia).toString()),
                    atual.getYear(), atual.getMonthValue() - 1, atual.getDayOfMonth()).show();
        });
    }

    public static void hora(EditText campo) {
        campo.setOnClickListener(v -> {
            LocalTime atual = valor(campo) != null ? LocalTime.parse(valor(campo)) : LocalTime.of(8, 0);
            new TimePickerDialog(campo.getContext(),
                    (seletor, hora, minuto) -> definirHora(campo, LocalTime.of(hora, minuto).toString()),
                    atual.getHour(), atual.getMinute(), true).show();
        });
    }

    public static void definirData(EditText campo, String iso) {
        campo.setTag(iso);
        campo.setText(Formatador.data(iso));
    }

    public static void definirHora(EditText campo, String hora) {
        campo.setTag(hora);
        campo.setText(hora);
    }

    public static String valor(EditText campo) {
        return (String) campo.getTag();
    }
}
