package com.hospedagem.sistema_hospedagem.notificacao;

import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import com.hospedagem.sistema_hospedagem.model.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class CanalEmail implements CanalNotificacao {

    @Override
    public void notificar(Notificacao n) {
        String saida = String.format("[E-MAIL -> %s] %s",
                n.getDestinatario(), n.getMensagem());
        System.out.println(saida);
        RegistroDeLogs.getInstance().info("Notificacao enviada via E-MAIL: " + n.getMensagem());
    }

    @Override
    public String getNome() {
        return "E-MAIL";
    }
}