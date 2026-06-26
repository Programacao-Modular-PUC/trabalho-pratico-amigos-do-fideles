package com.hospedagem.sistema_hospedagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import com.hospedagem.sistema_hospedagem.model.QuartoFamilia;

@Entity
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private int qtdDiarias;
    private Integer qtdHospedes;
    private Double valorFinal;
    private String status;

    @Enumerated(EnumType.STRING)
    private TipoTarifa tipoTarifa = TipoTarifa.PADRAO;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "quarto_id")
    private Quarto quarto;

    public Aluguel() {
        this.status = "ATIVO";
    }

    public Aluguel(LocalDateTime dataEntrada, LocalDateTime dataSaida, Integer qtdHospedes,
            Cliente cliente, Quarto quarto) {
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.qtdHospedes = qtdHospedes;
        this.cliente = cliente;
        this.quarto = quarto;
        this.status = "ATIVO";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public int getQtdDiarias() { return qtdDiarias; }
    public void setQtdDiarias(int qtdDiarias) { this.qtdDiarias = qtdDiarias; }

    public Integer getQtdHospedes() { return qtdHospedes; }
    public void setQtdHospedes(Integer qtdHospedes) { this.qtdHospedes = qtdHospedes; }

    public Double getValorFinal() { return valorFinal; }
    public void setValorFinal(Double valorFinal) { this.valorFinal = valorFinal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public TipoTarifa getTipoTarifa() { return tipoTarifa; }
    public void setTipoTarifa(TipoTarifa tipoTarifa) {
        this.tipoTarifa = (tipoTarifa == null) ? TipoTarifa.PADRAO : tipoTarifa;
    }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public void cancelar() {
        this.status = "CANCELADO";
    }

    public boolean isAtivo() {
        return "ATIVO".equals(this.status);
    }

    public int calcularDiarias() {
        if (dataEntrada == null || dataSaida == null) return 0;

        int diarias = 0;
        LocalDateTime entrada = dataEntrada;

        if (dataEntrada.getHour() > 12) {
            diarias++;
            entrada = dataEntrada.plusDays(1).withHour(12).withMinute(0);
        }

        LocalDateTime saida = dataSaida;
        if (dataSaida.getHour() > 12) {
            diarias++;
            saida = dataSaida.withHour(12).withMinute(0);
        }

        diarias += (int) java.time.Duration.between(entrada, saida).toDays();
        return diarias;
    }

    public Double calcularValorFinal() {
        this.qtdDiarias = calcularDiarias();

        if (quarto instanceof QuartoFamilia && qtdHospedes != null) {
            QuartoFamilia qf = (QuartoFamilia) quarto;
            this.valorFinal = qf.calcularDiariaComHospedes(qtdHospedes.intValue()) * qtdDiarias;
        } else {
            this.valorFinal = quarto.calcularValorFinal(qtdDiarias);
        }

        return valorFinal;
    }
}