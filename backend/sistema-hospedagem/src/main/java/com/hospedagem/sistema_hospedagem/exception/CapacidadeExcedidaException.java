package com.hospedagem.sistema_hospedagem.exception;

public class CapacidadeExcedidaException extends RuntimeException {

    public CapacidadeExcedidaException(String mensagem) {
        super(mensagem);
    }

    public CapacidadeExcedidaException(int qtdHospedes, int capacidadeMaxima) {
        super("Número de hóspedes (" + qtdHospedes + ") excede a capacidade máxima do quarto (" + capacidadeMaxima + ").");
    }
}
