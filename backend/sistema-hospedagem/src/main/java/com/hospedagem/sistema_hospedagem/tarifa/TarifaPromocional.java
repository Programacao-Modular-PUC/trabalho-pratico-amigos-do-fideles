package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaPromocional implements TarifaStrategy {

    private static final double FATOR = 0.70;

    @Override
    public double aplicar(double valorBruto) {
        return valorBruto * FATOR;
    }

    @Override
    public TipoTarifa getTipo() {
        return TipoTarifa.PROMOCIONAL;
    }

    @Override
    public String getDescricao() {
        return "Promocao temporaria: desconto de 30%";
    }
}