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

    @Override
    public Double calcularDiaria() {
        return getValorBase() * (1 + percentualAdicional / 100);
    }

    public Double calcularDiariaComHospedes(int qtdHospedes) {
        double total = getValorBase() * (1 + percentualAdicional / 100);
        total += qtdHospedes * valorPorHospede;
        return total;
    }

    public Double calcularDesconto(int qtdHospedes) {
        if (qtdHospedes >= capacidadeMaxima) {
            return 0.10; // 10% de desconto para grupos
        }
        return 0.0;
    }
}