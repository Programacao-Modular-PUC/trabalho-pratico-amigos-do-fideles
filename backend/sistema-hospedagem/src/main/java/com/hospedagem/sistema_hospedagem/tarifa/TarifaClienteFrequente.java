package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaClienteFrequente implements TarifaStrategy {

    private static final double FATOR = 0.90;

    @Override
    public double aplicar(double valorBruto) {
        return valorBruto * FATOR;
    }

    @Override
    public TipoTarifa getTipo() {
        return TipoTarifa.CLIENTE_FREQUENTE;
    }

    @Override
    public String getDescricao() {
        return "Cliente frequente: desconto de 10%";
    }
}