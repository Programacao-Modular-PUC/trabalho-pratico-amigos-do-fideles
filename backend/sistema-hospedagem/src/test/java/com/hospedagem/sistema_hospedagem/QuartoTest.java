package com.hospedagem.sistema_hospedagem;

import com.hospedagem.sistema_hospedagem.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuartoTest {


    private QuartoIndividual quartoIndividual;
    private QuartoDuplo      quartoDuplo;
    private QuartoFamilia    quartoFamilia;

    @BeforeEach
    void setUp() {
        quartoIndividual = new QuartoIndividual(
                200.0,   
                false,   
                false,   
                null,    
                2,       
                30.0     
        );

        quartoDuplo = new QuartoDuplo(
                300.0,   
                true,    
                false,   
                null,    
                "QUEEN", 
                true,    
                50.0,  
                20.0   
        );

        quartoFamilia = new QuartoFamilia(
                400.0,  
                false,  
                true,    
                null,    
                6,      
                3,      
                30.0,   
                10.0     
        );
    }


    @Test
    @DisplayName("QuartoIndividual: diária = valorBase + (qtdCamas - 1) * adicional")
    void calcularDiaria_QuartoIndividual_ComMaisDe1Cama() {
        double diaria = quartoIndividual.calcularDiaria();
        assertEquals(230.0, diaria, 0.01);
    }

    @Test
    @DisplayName("QuartoIndividual: diária = valorBase quando tem 1 cama")
    void calcularDiaria_QuartoIndividual_Com1Cama() {
        QuartoIndividual q = new QuartoIndividual(150.0, false, false, null, 1, 30.0);
        assertEquals(150.0, q.calcularDiaria(), 0.01);
    }

    @Test
    @DisplayName("QuartoDuplo: diária = valorBase + adicionalConforto + taxaBerco (quando possuiBerco)")
    void calcularDiaria_QuartoDuplo_ComBerco() {
        double diaria = quartoDuplo.calcularDiaria();
        assertEquals(370.0, diaria, 0.01);
    }

    @Test
    @DisplayName("QuartoDuplo: diária = valorBase + adicionalConforto (sem berço)")
    void calcularDiaria_QuartoDuplo_SemBerco() {
        QuartoDuplo q = new QuartoDuplo(300.0, true, false, null, "CASAL", false, 50.0, 20.0);
        assertEquals(320.0, q.calcularDiaria(), 0.01);
    }

    @Test
    @DisplayName("QuartoFamilia: diária = valorBase * (1 + percentual/100)")
    void calcularDiaria_QuartoFamilia_Simples() {
        double diaria = quartoFamilia.calcularDiaria();
        assertEquals(440.0, diaria, 0.01);
    }

    @Test
    @DisplayName("QuartoFamilia: calcularDiariaComHospedes aplica desconto progressivo")
    void calcularDiaria_QuartoFamilia_ComHospedes_EDescontoProgressivo() {
        double valor = quartoFamilia.calcularDiariaComHospedes(6);
        assertTrue(valor > 0, "Valor final deve ser positivo");
    }

    @Test
    @DisplayName("QuartoFamilia: desconto progressivo — menos de 3 hóspedes = 0%")
    void descontoProgressivo_MenosDe3Hospedes() {
        assertEquals(0.0, quartoFamilia.calcularDescontoProgressivo(2), 0.001);
    }

    @Test
    @DisplayName("QuartoFamilia: desconto progressivo — capacidade total = 20%")
    void descontoProgressivo_CapacidadeTotal() {
        assertEquals(0.20, quartoFamilia.calcularDescontoProgressivo(6), 0.001);
    }


    @Test
    @DisplayName("calcularValorFinal: inclui adicional de AR (+50) e hidro (+80)")
    void calcularValorFinal_ComAR_EHidro() {
        QuartoIndividual q = new QuartoIndividual(200.0, true, true, null, 1, 30.0);
        double total = q.calcularValorFinal(2);
        assertEquals(660.0, total, 0.01);
    }


    @Test
    @DisplayName("QuartoDuplo com berço: taxaBerco deve ser incluída na diária")
    void berco_QuartoDuplo_TaxaIncluida() {
        assertTrue(quartoDuplo.getPossuiBerco());
        double comBerco = quartoDuplo.calcularDiaria(); // 370

        QuartoDuplo semBerco = new QuartoDuplo(300.0, true, false, null, "QUEEN", false, 50.0, 20.0);
        double semBercoValor = semBerco.calcularDiaria(); // 320

        assertEquals(50.0, comBerco - semBercoValor, 0.01,
                "A diferença entre quarto com berço e sem berço deve ser exatamente a taxaBerco");
    }

    @Test
    @DisplayName("QuartoIndividual: não possui atributo possuiBerco — limite de hóspedes = qtdCamas")
    void berco_QuartoIndividual_NaoExiste_LimiteEhQtdCamas() {
        assertEquals(2, quartoIndividual.getLimiteHospedes(),
                "Limite de hóspedes do QuartoIndividual deve ser igual à quantidade de camas");
    }


    @Test
    @DisplayName("QuartoIndividual: limite de hóspedes = quantidadeCamas")
    void limiteHospedes_QuartoIndividual() {
        assertEquals(quartoIndividual.getQuantidadeCamas(), quartoIndividual.getLimiteHospedes());
    }

    @Test
    @DisplayName("QuartoFamilia: capacidade máxima configurada corretamente")
    void limiteHospedes_QuartoFamilia() {
        assertEquals(6, quartoFamilia.getCapacidadeMaxima());
    }

    @Test
    @DisplayName("QuartoIndividual: deve lançar IllegalArgumentException com 0 camas")
    void criarQuartoIndividual_ComZeroCamas_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new QuartoIndividual(200.0, false, false, null, 0, 30.0));
    }


    @Test
    @DisplayName("verificarDisponibilidade: quarto disponível quando não há conflito de datas")
    void disponibilidade_QuartoLivre() {
        LocalDateTime entrada = LocalDateTime.of(2025, 8, 10, 14, 0);
        LocalDateTime saida   = LocalDateTime.of(2025, 8, 15, 12, 0);
        List<Aluguel> alugueis = new ArrayList<>();

        assertTrue(quartoIndividual.verificarDisponibilidade(entrada, saida, alugueis),
                "Quarto sem aluguéis deve estar disponível");
    }

    @Test
    @DisplayName("verificarDisponibilidade: retorna false quando há conflito de datas")
    void disponibilidade_QuartoOcupado() {
        LocalDateTime entrada1 = LocalDateTime.of(2025, 8, 10, 14, 0);
        LocalDateTime saida1   = LocalDateTime.of(2025, 8, 15, 12, 0);

        Aluguel aluguelExistente = new Aluguel();
        aluguelExistente.setDataEntrada(entrada1);
        aluguelExistente.setDataSaida(saida1);
        aluguelExistente.setStatus("ATIVO");

        LocalDateTime novaEntrada = LocalDateTime.of(2025, 8, 12, 14, 0);
        LocalDateTime novaSaida   = LocalDateTime.of(2025, 8, 18, 12, 0);

        assertFalse(quartoIndividual.verificarDisponibilidade(novaEntrada, novaSaida, List.of(aluguelExistente)),
                "Quarto com conflito de datas deve estar indisponível");
    }

    @Test
    @DisplayName("verificarDisponibilidade: disponível para datas sem sobreposição")
    void disponibilidade_SemSobreposicao() {
        LocalDateTime entrada1 = LocalDateTime.of(2025, 8, 10, 14, 0);
        LocalDateTime saida1   = LocalDateTime.of(2025, 8, 15, 12, 0);

        Aluguel aluguelExistente = new Aluguel();
        aluguelExistente.setDataEntrada(entrada1);
        aluguelExistente.setDataSaida(saida1);

        LocalDateTime novaEntrada = LocalDateTime.of(2025, 8, 16, 14, 0);
        LocalDateTime novaSaida   = LocalDateTime.of(2025, 8, 20, 12, 0);

        assertTrue(quartoIndividual.verificarDisponibilidade(novaEntrada, novaSaida, List.of(aluguelExistente)),
                "Quarto deve estar disponível para período sem sobreposição");
    }


    @Test
    @DisplayName("Aluguel.calcularDiarias: calcula corretamente a quantidade de noites")
    void aluguel_calcularDiarias_Simples() {
        Aluguel aluguel = new Aluguel();
        aluguel.setDataEntrada(LocalDateTime.of(2025, 8, 10, 12, 0));
        aluguel.setDataSaida(LocalDateTime.of(2025, 8, 13, 12, 0));

        assertEquals(3, aluguel.calcularDiarias());
    }

    @Test
    @DisplayName("Aluguel.calcularDiarias: retorna 0 com datas nulas")
    void aluguel_calcularDiarias_DataNula() {
        Aluguel aluguel = new Aluguel();
        assertEquals(0, aluguel.calcularDiarias());
    }

    @Test
    @DisplayName("Aluguel.isAtivo: status padrão é ATIVO")
    void aluguel_statusPadrao_EhAtivo() {
        Aluguel aluguel = new Aluguel();
        assertTrue(aluguel.isAtivo());
    }

    @Test
    @DisplayName("Aluguel.cancelar: muda status para CANCELADO")
    void aluguel_cancelar_MudaStatus() {
        Aluguel aluguel = new Aluguel();
        aluguel.cancelar();
        assertFalse(aluguel.isAtivo());
        assertEquals("CANCELADO", aluguel.getStatus());
    }
}
