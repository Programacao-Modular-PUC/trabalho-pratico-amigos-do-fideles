package com.hospedagem.sistema_hospedagem;

import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import com.hospedagem.sistema_hospedagem.tarifa.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TarifaStrategyTest {

    private static final double DELTA = 0.001;

    @Test
    @DisplayName("Cada estratégia aplica o fator correto sobre o valor bruto")
    void estrategias_AplicamFatorCorreto() {
        assertEquals(100.0, new TarifaPadrao().aplicar(100.0), DELTA);
        assertEquals(130.0, new TarifaAltaTemporada().aplicar(100.0), DELTA);
        assertEquals(85.0,  new TarifaBaixaTemporada().aplicar(100.0), DELTA);
        assertEquals(150.0, new TarifaFeriado().aplicar(100.0), DELTA);
        assertEquals(70.0,  new TarifaPromocional().aplicar(100.0), DELTA);
        assertEquals(90.0,  new TarifaClienteFrequente().aplicar(100.0), DELTA);
    }

    @Test
    @DisplayName("Factory resolve a estratégia correta para cada TipoTarifa")
    void factory_ResolveEstrategiaCorreta() {
        TarifaStrategyFactory factory = new TarifaStrategyFactory(List.of(
                new TarifaPadrao(), new TarifaAltaTemporada(), new TarifaBaixaTemporada(),
                new TarifaFeriado(), new TarifaPromocional(), new TarifaClienteFrequente()));

        assertEquals(TipoTarifa.FERIADO, factory.resolver(TipoTarifa.FERIADO).getTipo());
        assertEquals(150.0, factory.resolver(TipoTarifa.FERIADO).aplicar(100.0), DELTA);
    }

    @Test
    @DisplayName("Factory retorna tarifa PADRAO como fallback para tipo nulo")
    void factory_FallbackParaPadrao() {
        TarifaStrategyFactory factory = new TarifaStrategyFactory(List.of(
                new TarifaPadrao(), new TarifaAltaTemporada()));
        assertEquals(TipoTarifa.PADRAO, factory.resolver(null).getTipo());
    }
}