package com.hospedagem.sistema_hospedagem.tarifa;

import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class TarifaStrategyFactory {

    private final Map<TipoTarifa, TarifaStrategy> estrategias = new EnumMap<>(TipoTarifa.class);

    public TarifaStrategyFactory(List<TarifaStrategy> todasAsEstrategias) {
        for (TarifaStrategy estrategia : todasAsEstrategias) {
            estrategias.put(estrategia.getTipo(), estrategia);
        }
        RegistroDeLogs.getInstance()
                .info("TarifaStrategyFactory carregada com " + estrategias.size() + " estrategias.");
    }

    public TarifaStrategy resolver(TipoTarifa tipo) {
        TipoTarifa alvo = (tipo == null) ? TipoTarifa.PADRAO : tipo;
        return estrategias.getOrDefault(alvo, estrategias.get(TipoTarifa.PADRAO));
    }

    public Map<TipoTarifa, TarifaStrategy> getEstrategias() {
        return estrategias;
    }
}