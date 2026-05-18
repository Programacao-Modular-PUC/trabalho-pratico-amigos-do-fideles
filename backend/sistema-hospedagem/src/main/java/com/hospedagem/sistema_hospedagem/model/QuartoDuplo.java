package com.hospedagem.sistema_hospedagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_duplo")
public class QuartoDuplo extends Quarto {

    private String tipoCama; // "CASAL", "QUEEN", "KING"
    private Boolean possuiBerco;
    private Double taxaBerco;
    private Double adicionalConforto; // adicional por tipo de cama

    public QuartoDuplo() {
    }

    public QuartoDuplo(Double valorBase, Boolean possuiAR, Boolean possuiHidro, Residencia residencia,
            String tipoCama, Boolean possuiBerco, Double taxaBerco, Double adicionalConforto) {
        super(valorBase, possuiAR, possuiHidro, residencia);
        this.tipoCama = tipoCama;
        this.possuiBerco = possuiBerco;
        this.taxaBerco = taxaBerco;
        this.adicionalConforto = adicionalConforto;
    }

    public String getTipoCama() {
        return tipoCama;
    }

    public void setTipoCama(String tipoCama) {
        this.tipoCama = tipoCama;
    }

    public Boolean getPossuiBerco() {
        return possuiBerco;
    }

    public void setPossuiBerco(Boolean possuiBerco) {
        this.possuiBerco = possuiBerco;
    }

    public Double getTaxaBerco() {
        return taxaBerco;
    }

    public void setTaxaBerco(Double taxaBerco) {
        this.taxaBerco = taxaBerco;
    }

    public Double getAdicionalConforto() {
        return adicionalConforto;
    }

    public void setAdicionalConforto(Double adicionalConforto) {
        this.adicionalConforto = adicionalConforto;
    }

    @Override
    public Double calcularDiaria() {
        double total = getValorBase();

        if (adicionalConforto != null) {
            total += adicionalConforto;
        }

        if (possuiBerco != null && possuiBerco && taxaBerco != null) {
            total += taxaBerco;
        }

        return total;
    }
}
