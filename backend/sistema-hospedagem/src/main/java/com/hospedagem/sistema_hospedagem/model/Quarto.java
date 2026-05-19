package com.hospedagem.sistema_hospedagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "quartos")
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double valorBase;
    private Boolean possuiAR;
    private Boolean possuiHidro;

    @ManyToOne
    @JoinColumn(name = "residencia_id")
    private Residencia residencia;

    public Quarto() {
    }

    public Quarto(Double valorBase, Boolean possuiAR, Boolean possuiHidro, Residencia residencia) {
        this.valorBase = valorBase;
        this.possuiAR = possuiAR;
        this.possuiHidro = possuiHidro;
        this.residencia = residencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public Boolean getPossuiAR() {
        return possuiAR;
    }

    public void setPossuiAR(Boolean possuiAR) {
        this.possuiAR = possuiAR;
    }

    public Boolean getPossuiHidro() {
        return possuiHidro;
    }

    public void setPossuiHidro(Boolean possuiHidro) {
        this.possuiHidro = possuiHidro;
    }

    public Residencia getResidencia() {
        return residencia;
    }

    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }

    // Calculo simples da diaria sem parametros extras (implementado por cada subtipo)
    public abstract Double calcularDiaria();

    // Calculo padrao do valor final: usa calcularDiaria() + comodidades × diarias
    // Subtipos que precisam de logica diferente (ex: QuartoFamilia) sobrescrevem este metodo
    public Double calcularValorFinal(int qtdDiarias, int qtdHospedes) {
        double adicional = 0;
        if (possuiAR != null && possuiAR) adicional += 50;
        if (possuiHidro != null && possuiHidro) adicional += 80;
        return (calcularDiaria() + adicional) * qtdDiarias;
    }
}
