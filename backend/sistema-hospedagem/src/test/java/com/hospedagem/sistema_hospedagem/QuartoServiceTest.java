package com.hospedagem.sistema_hospedagem;

import com.hospedagem.sistema_hospedagem.exception.RecursoNaoPermitidoException;
import com.hospedagem.sistema_hospedagem.model.*;
import com.hospedagem.sistema_hospedagem.repository.QuartoRepository;
import com.hospedagem.sistema_hospedagem.service.QuartoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class QuartoServiceTest {

    @Mock
    private QuartoRepository quartoRepository;

    @InjectMocks
    private QuartoService quartoService;

    private QuartoIndividual individual;
    private QuartoDuplo      duplo;
    private QuartoFamilia    familia;

    @BeforeEach
    void setUp() {
        individual = new QuartoIndividual(200.0, false, false, null, 1, 30.0);
        duplo      = new QuartoDuplo(300.0, false, false, null, "CASAL", true, 50.0, 20.0);
        familia    = new QuartoFamilia(400.0, false, false, null, 6, 3, 30.0, 10.0);
    }


    @Test
    @DisplayName("listarPorTipo: retorna apenas quartos individuais")
    void listarPorTipo_Individual() {
        when(quartoRepository.findAll()).thenReturn(List.of(individual, duplo, familia));

        List<Quarto> resultado = quartoService.listarPorTipo("individual");

        assertEquals(1, resultado.size());
        assertInstanceOf(QuartoIndividual.class, resultado.get(0));
    }

    @Test
    @DisplayName("listarPorTipo: retorna apenas quartos duplos")
    void listarPorTipo_Duplo() {
        when(quartoRepository.findAll()).thenReturn(List.of(individual, duplo, familia));

        List<Quarto> resultado = quartoService.listarPorTipo("duplo");

        assertEquals(1, resultado.size());
        assertInstanceOf(QuartoDuplo.class, resultado.get(0));
    }

    @Test
    @DisplayName("listarPorTipo: retorna apenas quartos família")
    void listarPorTipo_Familia() {
        when(quartoRepository.findAll()).thenReturn(List.of(individual, duplo, familia));

        List<Quarto> resultado = quartoService.listarPorTipo("familia");

        assertEquals(1, resultado.size());
        assertInstanceOf(QuartoFamilia.class, resultado.get(0));
    }

    @Test
    @DisplayName("listarPorTipo: tipo inválido lança IllegalArgumentException")
    void listarPorTipo_TipoInvalido_DeveLancarExcecao() {
        when(quartoRepository.findAll()).thenReturn(List.of(individual));

        assertThrows(IllegalArgumentException.class,
                () -> quartoService.listarPorTipo("suite"));
    }


    @Test
    @DisplayName("validarBercoEmIndividual: lança RecursoNaoPermitidoException quando solicitouBerco=true")
    void berco_EmQuartoIndividual_DeveLancarExcecao() {
        assertThrows(RecursoNaoPermitidoException.class,
                () -> quartoService.validarBercoEmIndividual(true));
    }

    @Test
    @DisplayName("validarBercoEmIndividual: não lança exceção quando solicitouBerco=false")
    void berco_NaoSolicitado_NaoLancaExcecao() {
        assertDoesNotThrow(() -> quartoService.validarBercoEmIndividual(false));
    }
}
