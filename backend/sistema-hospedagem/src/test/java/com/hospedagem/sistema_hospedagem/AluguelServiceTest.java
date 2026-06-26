package com.hospedagem.sistema_hospedagem;

import com.hospedagem.sistema_hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.sistema_hospedagem.exception.DataInvalidaException;
import com.hospedagem.sistema_hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.sistema_hospedagem.model.*;
import com.hospedagem.sistema_hospedagem.notificacao.CentralDeNotificacoes;
import com.hospedagem.sistema_hospedagem.repository.AluguelRepository;
import com.hospedagem.sistema_hospedagem.repository.ClienteRepository;
import com.hospedagem.sistema_hospedagem.repository.QuartoRepository;
import com.hospedagem.sistema_hospedagem.service.AluguelService;
import com.hospedagem.sistema_hospedagem.tarifa.TarifaStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AluguelServiceTest {

    @Mock private AluguelRepository aluguelRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private QuartoRepository  quartoRepository;
    @Mock private TarifaStrategyFactory tarifaStrategyFactory;
    @Mock private CentralDeNotificacoes centralDeNotificacoes;

    @InjectMocks
    private AluguelService aluguelService;

    private Cliente cliente;
    private QuartoIndividual quartoIndividual;
    private QuartoFamilia    quartoFamilia;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("João Silva", "123.456.789-00", "Rua A, 1", "31999999999", "joao@email.com");
        cliente.setId(1L);

        quartoIndividual = new QuartoIndividual(200.0, false, false, null, 2, 30.0);
        quartoIndividual.setId(10L);

        quartoFamilia = new QuartoFamilia(400.0, false, false, null, 6, 3, 30.0, 10.0);
        quartoFamilia.setId(20L);
    }

    @Test
    @DisplayName("criar: lança QuartoIndisponivelException quando quarto já está ocupado")
    void criar_DeveLancar_QuartoIndisponivelException() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(5);
        LocalDateTime saida   = LocalDateTime.now().plusDays(10);

        Aluguel existente = new Aluguel(
                LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusDays(9),
                1, cliente, quartoIndividual);
        existente.setStatus("ATIVO");

        Aluguel novoAluguel = new Aluguel(entrada, saida, 1, cliente, quartoIndividual);
        novoAluguel.setCliente(cliente);
        novoAluguel.setQuarto(quartoIndividual);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quartoIndividual));
        when(aluguelRepository.findByQuartoId(10L)).thenReturn(List.of(existente));

        assertThrows(QuartoIndisponivelException.class, () -> aluguelService.criar(novoAluguel));
    }

    @Test
    @DisplayName("criar: quarto disponível é salvo com sucesso")
    void criar_QuartoDisponivel_SalvaComSucesso() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(5);
        LocalDateTime saida   = LocalDateTime.now().plusDays(10);

        Aluguel novoAluguel = new Aluguel(entrada, saida, 1, cliente, quartoIndividual);
        novoAluguel.setCliente(cliente);
        novoAluguel.setQuarto(quartoIndividual);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quartoIndividual));
        when(aluguelRepository.findByQuartoId(10L)).thenReturn(new ArrayList<>());
        when(aluguelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Aluguel resultado = aluguelService.criar(novoAluguel);

        assertNotNull(resultado);
        assertEquals("ATIVO", resultado.getStatus());
        verify(aluguelRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("criar: lança CapacidadeExcedidaException para QuartoFamilia com hóspedes acima do limite")
    void criar_DeveLancar_CapacidadeExcedidaException_QuartoFamilia() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(5);
        LocalDateTime saida   = LocalDateTime.now().plusDays(10);

        Aluguel aluguel = new Aluguel(entrada, saida, 10, cliente, quartoFamilia);
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quartoFamilia);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(20L)).thenReturn(Optional.of(quartoFamilia));
        when(aluguelRepository.findByQuartoId(20L)).thenReturn(new ArrayList<>());

        assertThrows(CapacidadeExcedidaException.class, () -> aluguelService.criar(aluguel));
    }

    @Test
    @DisplayName("criar: lança CapacidadeExcedidaException para QuartoIndividual com hóspedes acima do limite")
    void criar_DeveLancar_CapacidadeExcedidaException_QuartoIndividual() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(5);
        LocalDateTime saida   = LocalDateTime.now().plusDays(10);

        Aluguel aluguel = new Aluguel(entrada, saida, 5, cliente, quartoIndividual);
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quartoIndividual);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quartoIndividual));
        when(aluguelRepository.findByQuartoId(10L)).thenReturn(new ArrayList<>());

        assertThrows(CapacidadeExcedidaException.class, () -> aluguelService.criar(aluguel));
    }

    @Test
    @DisplayName("criar: lança DataInvalidaException quando dataEntrada >= dataSaida")
    void criar_DeveLancar_DataInvalidaException_EntradaAposSaida() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(10);
        LocalDateTime saida   = LocalDateTime.now().plusDays(5);

        Aluguel aluguel = new Aluguel(entrada, saida, 1, cliente, quartoIndividual);
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quartoIndividual);

        assertThrows(DataInvalidaException.class, () -> aluguelService.criar(aluguel));
    }

    @Test
    @DisplayName("criar: lança DataInvalidaException quando data de entrada é nula")
    void criar_DeveLancar_DataInvalidaException_DataNula() {
        Aluguel aluguel = new Aluguel(null, LocalDateTime.now().plusDays(5), 1, cliente, quartoIndividual);
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quartoIndividual);

        assertThrows(DataInvalidaException.class, () -> aluguelService.criar(aluguel));
    }

    @Test
    @DisplayName("criar: lança DataInvalidaException quando data de entrada está no passado")
    void criar_DeveLancar_DataInvalidaException_DataNoPassado() {
        LocalDateTime entrada = LocalDateTime.now().minusDays(2);
        LocalDateTime saida   = LocalDateTime.now().plusDays(3);

        Aluguel aluguel = new Aluguel(entrada, saida, 1, cliente, quartoIndividual);
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quartoIndividual);

        assertThrows(DataInvalidaException.class, () -> aluguelService.criar(aluguel));
    }

    @Test
    @DisplayName("cancelar: muda status para CANCELADO")
    void cancelar_Aluguel_ComSucesso() {
        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setStatus("ATIVO");

        when(aluguelRepository.findById(1L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Aluguel cancelado = aluguelService.cancelar(1L);

        assertEquals("CANCELADO", cancelado.getStatus());
        assertFalse(cancelado.isAtivo());
    }

    @Test
    @DisplayName("cancelar: lança IllegalArgumentException para aluguel já cancelado")
    void cancelar_JaCancelado_DeveLancarExcecao() {
        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setStatus("CANCELADO");

        when(aluguelRepository.findById(1L)).thenReturn(Optional.of(aluguel));

        assertThrows(IllegalArgumentException.class, () -> aluguelService.cancelar(1L));
    }

    @Test
    @DisplayName("listarHistoricoCliente: retorna todos os aluguéis do cliente")
    void listarHistoricoCliente_RetornaAlugueis() {
        Aluguel a1 = new Aluguel(); a1.setStatus("ATIVO");
        Aluguel a2 = new Aluguel(); a2.setStatus("CANCELADO");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(List.of(a1, a2));

        List<Aluguel> historico = aluguelService.listarHistoricoCliente(1L);

        assertEquals(2, historico.size());
    }

    @Test
    @DisplayName("listarHistoricoCliente: lança RuntimeException para cliente inexistente")
    void listarHistoricoCliente_ClienteNaoEncontrado_DeveLancarExcecao() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> aluguelService.listarHistoricoCliente(99L));
    }

    @Test
    @DisplayName("verificarDisponibilidade: ignora aluguéis cancelados ao checar conflito")
    void disponibilidade_IgnoraAluguelCancelado() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(5);
        LocalDateTime saida   = LocalDateTime.now().plusDays(10);

        Aluguel cancelado = new Aluguel(
                LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusDays(9),
                1, cliente, quartoIndividual);
        cancelado.setStatus("CANCELADO");

        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quartoIndividual));
        when(aluguelRepository.findByQuartoId(10L)).thenReturn(List.of(cancelado));

        assertDoesNotThrow(() -> aluguelService.verificarDisponibilidade(10L, entrada, saida));
    }
}