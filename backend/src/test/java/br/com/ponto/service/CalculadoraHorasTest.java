package br.com.ponto.service;

import br.com.ponto.model.OrigemPonto;
import br.com.ponto.model.RegistroPonto;
import br.com.ponto.model.TipoMarcacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CalculadoraHorasTest {

    @Test
    void diaDeOitoHorasTemSaldoZero() {
        assertEquals(0, CalculadoraHoras.saldoDoDia(dia("08:00", "12:00", "13:00", "17:00")));
    }

    @Test
    void horaExtraGeraSaldoPositivo() {
        assertEquals(90, CalculadoraHoras.saldoDoDia(dia("08:00", "12:00", "13:00", "18:30")));
    }

    @Test
    void saidaAntecipadaGeraSaldoNegativo() {
        assertEquals(-60, CalculadoraHoras.saldoDoDia(dia("09:00", "12:00", "13:00", "17:00")));
    }

    @Test
    void diaIncompletoNaoTemSaldo() {
        assertNull(CalculadoraHoras.saldoDoDia(dia("08:00", "12:00")));
    }

    @Test
    void turnoNoturnoVirandoMeiaNoiteCalculaCorretamente() {
        assertEquals(135, CalculadoraHoras.minutosTrabalhados(dia("22:17", "23:00", "23:30", "01:02")));
        assertEquals(0, CalculadoraHoras.saldoDoDia(dia("22:00", "02:00", "03:00", "07:00")));
    }

    @Test
    void horariosIguaisCalculamSemErro() {
        assertEquals(13, CalculadoraHoras.minutosTrabalhados(dia("22:17", "22:17", "22:17", "22:30")));
    }

    private static List<RegistroPonto> dia(String... horas) {
        List<RegistroPonto> marcacoes = new ArrayList<>();
        for (int i = 0; i < horas.length; i++) {
            marcacoes.add(new RegistroPonto(null, LocalDate.now(), LocalTime.parse(horas[i]),
                    TipoMarcacao.values()[i], OrigemPonto.NORMAL, null));
        }
        return marcacoes;
    }
}
