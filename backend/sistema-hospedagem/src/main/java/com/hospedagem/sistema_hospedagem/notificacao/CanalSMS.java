package com.hospedagem.sistema_hospedagem.notificacao;

import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import com.hospedagem.sistema_hospedagem.model.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class CanalSMS implements CanalNotificacao {

    @Override
    public void notificar(Notificacao n) {
        String saida = String.format("[SMS -> %s] %s",
                n.getDestinatario(), n.getMensagem());
        System.out.println(saida);
        RegistroDeLogs.getInstance().info("Notificacao enviada via SMS: " + n.getMensagem());
    }

    @Override
    public String getNome() {
        return "SMS";
    }
}