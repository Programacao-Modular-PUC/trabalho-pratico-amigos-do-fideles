package com.hospedagem.sistema_hospedagem.notificacao;

import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import com.hospedagem.sistema_hospedagem.model.Notificacao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CentralDeNotificacoes {

    private final List<CanalNotificacao> canais = new ArrayList<>();
    private final List<Notificacao> historico = new ArrayList<>();

    public CentralDeNotificacoes(List<CanalNotificacao> canaisRegistrados) {
        this.canais.addAll(canaisRegistrados);
        RegistroDeLogs.getInstance()
                .info("CentralDeNotificacoes iniciada com " + canais.size() + " canais.");
    }

    public void adicionarCanal(CanalNotificacao canal) {
        if (canal != null && !canais.contains(canal)) {
            canais.add(canal);
        }
    }

    public void removerCanal(CanalNotificacao canal) {
        canais.remove(canal);
    }

    public void notificar(Notificacao notificacao) {
        historico.add(notificacao);
        RegistroDeLogs.getInstance()
                .info("Evento " + notificacao.getEvento() + " disparado para " + canais.size() + " canais.");
        for (CanalNotificacao canal : canais) {
            canal.notificar(notificacao);
        }
    }

    public List<Notificacao> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public List<String> getNomesDosCanais() {
        List<String> nomes = new ArrayList<>();
        for (CanalNotificacao c : canais) {
            nomes.add(c.getNome());
        }
        return nomes;
    }
}