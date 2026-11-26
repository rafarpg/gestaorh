package br.com.ponto.dto;

import br.com.ponto.model.RegistroPonto;
import br.com.ponto.model.TipoMarcacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record MarcacaoResponse(TipoMarcacao tipo, @JsonFormat(pattern = "HH:mm") LocalTime hora) {

    public static MarcacaoResponse de(RegistroPonto registro) {
        return new MarcacaoResponse(registro.getTipo(), registro.getHora());
    }
}
