package br.com.ponto.service;

import br.com.ponto.model.RegistroPonto;

import java.time.Duration;
import java.util.List;

public final class CalculadoraHoras {

    public static final int JORNADA_MINUTOS = 8 * 60;

    private CalculadoraHoras() {
    }

    public static Integer minutosTrabalhados(List<RegistroPonto> marcacoesDoDia) {
        if (marcacoesDoDia.size() != 4) {
            return null;
        }
        long manha = Duration.between(marcacoesDoDia.get(0).getHora(), marcacoesDoDia.get(1).getHora()).toMinutes();
        long tarde = Duration.between(marcacoesDoDia.get(2).getHora(), marcacoesDoDia.get(3).getHora()).toMinutes();
        return (int) (manha + tarde);
    }

    public static Integer saldoDoDia(List<RegistroPonto> marcacoesDoDia) {
        Integer trabalhados = minutosTrabalhados(marcacoesDoDia);
        return trabalhados == null ? null : trabalhados - JORNADA_MINUTOS;
    }
}
