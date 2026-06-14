package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.sistema_hospedagem.exception.DataInvalidaException;
import com.hospedagem.sistema_hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.sistema_hospedagem.model.Aluguel;
import com.hospedagem.sistema_hospedagem.model.Cliente;
import com.hospedagem.sistema_hospedagem.model.Quarto;
import com.hospedagem.sistema_hospedagem.model.QuartoFamilia;
import com.hospedagem.sistema_hospedagem.model.QuartoIndividual;
import com.hospedagem.sistema_hospedagem.repository.AluguelRepository;
import com.hospedagem.sistema_hospedagem.repository.ClienteRepository;
import com.hospedagem.sistema_hospedagem.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AluguelService {

    @Autowired
    private AluguelRepository aluguelRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    // ─── Listagem ─────────────────────────────────────────────────────────────

    public List<Aluguel> listarTodos() {
        return aluguelRepository.findAll();
    }

    public Optional<Aluguel> buscarPorId(Long id) {
        return aluguelRepository.findById(id);
    }

    public List<Aluguel> listarPorCliente(Long clienteId) {
        return aluguelRepository.findByClienteId(clienteId);
    }

    public List<Aluguel> listarPorQuarto(Long quartoId) {
        return aluguelRepository.findByQuartoId(quartoId);
    }

    /** Histórico de aluguéis ativos e cancelados de um cliente (Sprint 3). */
    public List<Aluguel> listarHistoricoCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId));
        return aluguelRepository.findByClienteId(clienteId);
    }

    // ─── Validações ───────────────────────────────────────────────────────────

    private void validarDatas(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        if (dataEntrada == null || dataSaida == null) {
            throw new DataInvalidaException("Data de entrada e data de saída são obrigatórias.");
        }
        if (!dataEntrada.isBefore(dataSaida)) {
            throw new DataInvalidaException("A data de entrada deve ser anterior à data de saída.");
        }
        if (dataEntrada.isBefore(LocalDateTime.now())) {
            throw new DataInvalidaException("A data de entrada não pode estar no passado.");
        }
    }

    public void verificarDisponibilidade(Long quartoId, LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        Quarto quarto = quartoRepository.findById(quartoId)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com id: " + quartoId));

        List<Aluguel> alugueis = aluguelRepository.findByQuartoId(quartoId)
                .stream()
                .filter(Aluguel::isAtivo) // ignora aluguéis cancelados
                .toList();

        if (!quarto.confirmarAluguel(dataEntrada, dataSaida, alugueis)) {
            throw new QuartoIndisponivelException(quartoId);
        }
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public Aluguel criar(Aluguel aluguel) {
        // 1. Validar datas
        validarDatas(aluguel.getDataEntrada(), aluguel.getDataSaida());

        // 2. Buscar cliente e quarto
        Cliente cliente = clienteRepository.findById(aluguel.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + aluguel.getCliente().getId()));

        Quarto quarto = quartoRepository.findById(aluguel.getQuarto().getId())
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com id: " + aluguel.getQuarto().getId()));

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);

        // 3. Verificar disponibilidade
        verificarDisponibilidade(quarto.getId(), aluguel.getDataEntrada(), aluguel.getDataSaida());

        // 4. Verificar capacidade de hóspedes
        if (quarto instanceof QuartoFamilia qf) {
            if (aluguel.getQtdHospedes() != null && aluguel.getQtdHospedes() > qf.getCapacidadeMaxima()) {
                throw new CapacidadeExcedidaException(aluguel.getQtdHospedes(), qf.getCapacidadeMaxima());
            }
        } else if (quarto instanceof QuartoIndividual qi) {
            if (aluguel.getQtdHospedes() != null && aluguel.getQtdHospedes() > qi.getLimiteHospedes()) {
                throw new CapacidadeExcedidaException(aluguel.getQtdHospedes(), qi.getLimiteHospedes());
            }
        }

        // 5. Calcular valor final e salvar
        aluguel.calcularValorFinal();
        return aluguelRepository.save(aluguel);
    }

    /** Cancela um aluguel (Sprint 3 - Cancelamento de aluguel). */
    public Aluguel cancelar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado com id: " + id));

        if (!aluguel.isAtivo()) {
            throw new IllegalArgumentException("Aluguel já está cancelado.");
        }

        aluguel.cancelar();
        return aluguelRepository.save(aluguel);
    }

    public void deletar(Long id) {
        aluguelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado com id: " + id));
        aluguelRepository.deleteById(id);
    }
}
