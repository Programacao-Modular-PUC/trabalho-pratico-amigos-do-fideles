package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaBaixaTemporada implements TarifaStrategy {

    private static final double FATOR = 0.85;

    @Override
    public double aplicar(double valorBruto) {
        return valorBruto * FATOR;
    }

    @Override
    public TipoTarifa getTipo() {
        return TipoTarifa.BAIXA_TEMPORADA;
    }

    @Override
    public String getDescricao() {
        return "Baixa temporada: desconto de 15%";
    }
}