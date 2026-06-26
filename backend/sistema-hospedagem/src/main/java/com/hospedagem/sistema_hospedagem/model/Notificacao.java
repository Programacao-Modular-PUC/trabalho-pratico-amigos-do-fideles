package com.hospedagem.sistema_hospedagem.model;

import java.time.LocalDateTime;

public class Notificacao {

    private final EventoReserva evento;
    private final String mensagem;
    private final String destinatario;
    private final LocalDateTime momento;

    public Notificacao(EventoReserva evento, String mensagem, String destinatario) {
        this.evento = evento;
        this.mensagem = mensagem;
        this.destinatario = destinatario;
        this.momento = LocalDateTime.now();
    }

    public EventoReserva getEvento() { return evento; }
    public String getMensagem() { return mensagem; }
    public String getDestinatario() { return destinatario; }
    public LocalDateTime getMomento() { return momento; }
}