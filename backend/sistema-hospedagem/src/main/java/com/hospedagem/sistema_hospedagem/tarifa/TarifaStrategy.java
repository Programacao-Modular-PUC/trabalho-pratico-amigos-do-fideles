package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;

public interface TarifaStrategy {

    double aplicar(double valorBruto);

    TipoTarifa getTipo();

    String getDescricao();
}