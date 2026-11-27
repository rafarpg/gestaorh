package br.com.ponto.service;

import br.com.ponto.model.RegistroPonto;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public final class CalculadoraHoras {

    public static final int JORNADA_MINUTOS = 8 * 60;
    public static final int MINUTOS_NO_DIA = 24 * 60;

    private CalculadoraHoras() {
    }

    public static Integer minutosTrabalhados(List<RegistroPonto> marcacoesDoDia) {
        if (marcacoesDoDia.size() != 4) {
            return null;
        }
        List<RegistroPonto> ordenadas = marcacoesDoDia.stream()
                .sorted(Comparator.comparingInt(r -> r.getTipo().ordinal()))
                .toList();
        long manha = duracaoMinutos(ordenadas.get(0).getHora(), ordenadas.get(1).getHora());
        long tarde = duracaoMinutos(ordenadas.get(2).getHora(), ordenadas.get(3).getHora());
        return (int) (manha + tarde);
    }

    public static Integer saldoDoDia(List<RegistroPonto> marcacoesDoDia) {
        Integer trabalhados = minutosTrabalhados(marcacoesDoDia);
        return trabalhados == null ? null : trabalhados - JORNADA_MINUTOS;
    }

    public static long duracaoMinutos(LocalTime inicio, LocalTime fim) {
        long minutos = Duration.between(inicio, fim).toMinutes();
        return minutos < 0 ? minutos + MINUTOS_NO_DIA : minutos;
    }
}
