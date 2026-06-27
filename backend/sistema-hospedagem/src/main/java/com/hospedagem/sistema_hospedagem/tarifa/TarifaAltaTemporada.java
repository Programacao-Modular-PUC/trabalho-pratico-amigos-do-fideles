package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaAltaTemporada implements TarifaStrategy {

    private static final double FATOR = 1.30;

    @Override
    public double aplicar(double valorBruto) {
        return valorBruto * FATOR;
    }

    @Override
    public TipoTarifa getTipo() {
        return TipoTarifa.ALTA_TEMPORADA;
    }

    @Override
    public String getDescricao() {
        return "Alta temporada: acrescimo de 30%";
    }
}