package com.hospedagem.sistema_hospedagem.exception;

public class QuartoIndisponivelException extends RuntimeException {

    public QuartoIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public QuartoIndisponivelException(Long quartoId) {
        super("Quarto de id " + quartoId + " não está disponível no período solicitado.");
    }
}
