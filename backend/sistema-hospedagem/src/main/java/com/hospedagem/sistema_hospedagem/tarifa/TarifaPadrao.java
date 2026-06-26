package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaPadrao implements TarifaStrategy {

    private static final double FATOR = 1.00;

    @Override
    public double aplicar(double valorBruto) {
        return valorBruto * FATOR;
    }

    @Override
    public TipoTarifa getTipo() {
        return TipoTarifa.PADRAO;
    }

    @Override
    public String getDescricao() {
        return "Tarifa padrao, sem ajuste de preco";
    }
}