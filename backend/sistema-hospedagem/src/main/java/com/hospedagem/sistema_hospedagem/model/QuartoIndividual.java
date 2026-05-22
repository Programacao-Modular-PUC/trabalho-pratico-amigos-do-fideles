package com.hospedagem.sistema_hospedagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_individual")
public class QuartoIndividual extends Quarto {

    private int quantidadeCamas;
    private Double valorAdicionalPorCama;

    public QuartoIndividual() {
    }

    public QuartoIndividual(Double valorBase, Boolean possuiAR, Boolean possuiHidro, Residencia residencia,
            int quantidadeCamas, Double valorAdicionalPorCama) {
        super(valorBase, possuiAR, possuiHidro, residencia);
        if (quantidadeCamas < 1) {
            throw new IllegalArgumentException("Quarto Individual deve ter ao menos 1 cama de solteiro.");
        }
        this.quantidadeCamas = quantidadeCamas;
        this.valorAdicionalPorCama = valorAdicionalPorCama;
    }

    public int getQuantidadeCamas() {
        return quantidadeCamas;
    }

    public void setQuantidadeCamas(int quantidadeCamas) {
        if (quantidadeCamas < 1) {
            throw new IllegalArgumentException("Quarto Individual deve ter ao menos 1 cama de solteiro.");
        }
        this.quantidadeCamas = quantidadeCamas;
    }

    public Double getValorAdicionalPorCama() {
        return valorAdicionalPorCama;
    }

    public void setValorAdicionalPorCama(Double valorAdicionalPorCama) {
        this.valorAdicionalPorCama = valorAdicionalPorCama;
    }

    // Quarto Individual nao possui berco — limite de hospedes proporcional as camas
    public int getLimiteHospedes() {
        return quantidadeCamas;
    }

    @Override
    public Double calcularDiaria() {
        double adicional = 0;
        if (quantidadeCamas > 1) {
            adicional = (quantidadeCamas - 1) * valorAdicionalPorCama;
        }
        return getValorBase() + adicional;
    }


}
