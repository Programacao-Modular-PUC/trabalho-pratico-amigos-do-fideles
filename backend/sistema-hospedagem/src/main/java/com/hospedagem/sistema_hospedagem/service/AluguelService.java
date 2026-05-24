package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.model.Aluguel;
import com.hospedagem.sistema_hospedagem.model.Cliente;
import com.hospedagem.sistema_hospedagem.model.Quarto;
import com.hospedagem.sistema_hospedagem.model.QuartoFamilia;
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

    public void verificarDisponibilidade(Long quartoId, LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        Quarto quarto = quartoRepository.findById(quartoId)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));
        List<Aluguel> alugueis = aluguelRepository.findByQuartoId(quartoId);
        if (!quarto.confirmarAluguel(dataEntrada, dataSaida, alugueis)) {
            throw new RuntimeException("Quarto já ocupado neste período!");
        }
    }

    public Aluguel criar(Aluguel aluguel) {
        Cliente cliente = clienteRepository.findById(aluguel.getCliente().getId())
                .orElseThrow(
                        () -> new RuntimeException("Cliente não encontrado com id: " + aluguel.getCliente().getId()));

        Quarto quarto = quartoRepository.findById(aluguel.getQuarto().getId())
                .orElseThrow(
                        () -> new RuntimeException("Quarto não encontrado com id: " + aluguel.getQuarto().getId()));

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);

        verificarDisponibilidade(quarto.getId(), aluguel.getDataEntrada(), aluguel.getDataSaida());

        if (quarto instanceof QuartoFamilia) {
            QuartoFamilia qf = (QuartoFamilia) quarto;
            if (aluguel.getQtdHospedes() != null && aluguel.getQtdHospedes() > qf.getCapacidadeMaxima()) {
                throw new RuntimeException("Número de hóspedes excede a capacidade máxima do quarto!");
            }
        }

        aluguel.calcularValorFinal();

        return aluguelRepository.save(aluguel);
    }

    public void deletar(Long id) {
        aluguelRepository.deleteById(id);
    }
}