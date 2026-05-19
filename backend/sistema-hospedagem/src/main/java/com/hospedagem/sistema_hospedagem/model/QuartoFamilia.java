package com.hospedagem.sistema_hospedagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_familia")
public class QuartoFamilia extends Quarto {

    private int capacidadeMaxima;
    private int quantidadeAmbientes;
    private Double valorPorHospede;
    private Double percentualAdicional; // percentual a mais sobre o valor base por hospede

    public QuartoFamilia() {
    }

    public QuartoFamilia(Double valorBase, Boolean possuiAR, Boolean possuiHidro, Residencia residencia,
            int capacidadeMaxima, int quantidadeAmbientes, Double valorPorHospede, Double percentualAdicional) {
        super(valorBase, possuiAR, possuiHidro, residencia);
        this.capacidadeMaxima = capacidadeMaxima;
        this.quantidadeAmbientes = quantidadeAmbientes;
        this.valorPorHospede = valorPorHospede;
        this.percentualAdicional = percentualAdicional;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getQuantidadeAmbientes() {
        return quantidadeAmbientes;
    }

    public void setQuantidadeAmbientes(int quantidadeAmbientes) {
        this.quantidadeAmbientes = quantidadeAmbientes;
    }

    public Double getValorPorHospede() {
        return valorPorHospede;
    }

    public void setValorPorHospede(Double valorPorHospede) {
        this.valorPorHospede = valorPorHospede;
    }

    public Double getPercentualAdicional() {
        return percentualAdicional;
    }

    public void setPercentualAdicional(Double percentualAdicional) {
        this.percentualAdicional = percentualAdicional;
    }

    // Calculo base sem hospedes (exigido pela assinatura abstrata)
    // Para QuartoFamilia, o calculo real e feito em calcularValorFinal()
    @Override
    public Double calcularDiaria() {
        return getValorBase() * (1 + percentualAdicional / 100);
    }

    // Calculo da diaria considerando numero de hospedes
    public Double calcularDiariaComHospedes(int qtdHospedes) {
        double diaria = getValorBase() * (1 + percentualAdicional / 100);
        diaria += qtdHospedes * valorPorHospede;
        return diaria;
    }

    // Retorna o percentual de desconto para grupos (0.0 a 1.0)
    // Desconto progressivo: 5% para metade da capacidade, 10% para capacidade cheia
    public double calcularDesconto(int qtdHospedes) {
        if (qtdHospedes >= capacidadeMaxima) {
            return 0.10;
        }
        if (qtdHospedes >= capacidadeMaxima / 2) {
            return 0.05;
        }
        return 0.0;
    }

    // Sobrescreve o calculo final aplicando hospedes e desconto progressivo
    @Override
    public Double calcularValorFinal(int qtdDiarias, int qtdHospedes) {
        double adicionalComodidades = 0;
        if (getPossuiAR() != null && getPossuiAR()) adicionalComodidades += 50;
        if (getPossuiHidro() != null && getPossuiHidro()) adicionalComodidades += 80;

        double diaria = calcularDiariaComHospedes(qtdHospedes) + adicionalComodidades;
        double desconto = calcularDesconto(qtdHospedes);
        return diaria * qtdDiarias * (1 - desconto);
    }
}
