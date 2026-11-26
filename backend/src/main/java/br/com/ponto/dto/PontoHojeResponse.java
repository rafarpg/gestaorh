package br.com.ponto.dto;

import br.com.ponto.model.TipoMarcacao;

import java.util.List;

public record PontoHojeResponse(
        List<MarcacaoResponse> marcacoes,
        TipoMarcacao proximaMarcacao,
        boolean manual,
        int bancoHorasMinutos) {
}
