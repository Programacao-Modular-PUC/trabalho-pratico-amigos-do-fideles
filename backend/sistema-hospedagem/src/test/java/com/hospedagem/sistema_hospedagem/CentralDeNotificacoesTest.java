package com.hospedagem.sistema_hospedagem;

import com.hospedagem.sistema_hospedagem.model.EventoReserva;
import com.hospedagem.sistema_hospedagem.model.Notificacao;
import com.hospedagem.sistema_hospedagem.notificacao.CanalNotificacao;
import com.hospedagem.sistema_hospedagem.notificacao.CentralDeNotificacoes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CentralDeNotificacoesTest {

    static class CanalEspiao implements CanalNotificacao {
        final List<Notificacao> recebidas = new ArrayList<>();
        public void notificar(Notificacao n) { recebidas.add(n); }
        public String getNome() { return "ESPIAO"; }
    }

    @Test
    @DisplayName("notificar entrega a notificação a TODOS os canais inscritos")
    void notificar_EntregaParaTodosOsCanais() {
        CanalEspiao c1 = new CanalEspiao();
        CanalEspiao c2 = new CanalEspiao();
        CentralDeNotificacoes central = new CentralDeNotificacoes(List.of(c1, c2));

        central.notificar(new Notificacao(EventoReserva.RESERVA_CRIADA, "msg", "cliente@x.com"));

        assertEquals(1, c1.recebidas.size());
        assertEquals(1, c2.recebidas.size());
        assertEquals(1, central.getHistorico().size());
    }

    @Test
    @DisplayName("removerCanal deixa de notificar o observer removido")
    void removerCanal_NaoNotificaRemovido() {
        CanalEspiao c1 = new CanalEspiao();
        CentralDeNotificacoes central = new CentralDeNotificacoes(new ArrayList<>(List.of(c1)));

        central.removerCanal(c1);
        central.notificar(new Notificacao(EventoReserva.RESERVA_CANCELADA, "msg", "x"));

        assertEquals(0, c1.recebidas.size());
    }
}