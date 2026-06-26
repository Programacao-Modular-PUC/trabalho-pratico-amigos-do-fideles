package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaFeriado implements TarifaStrategy {

    private static final double FATOR = 1.50;

    @Override
    public double aplicar(double valorBruto) {
        return valorBruto * FATOR;
    }

    @Override
    public TipoTarifa getTipo() {
        return TipoTarifa.FERIADO;
    }

    @Override
    public String getDescricao() {
        return "Feriado: acrescimo de 50%";
    }
}