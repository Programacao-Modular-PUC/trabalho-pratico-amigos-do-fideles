package com.hospedagem.sistema_hospedagem.notificacao;

import com.hospedagem.sistema_hospedagem.model.Notificacao;

public interface CanalNotificacao {

    void notificar(Notificacao notificacao);

    String getNome();
}