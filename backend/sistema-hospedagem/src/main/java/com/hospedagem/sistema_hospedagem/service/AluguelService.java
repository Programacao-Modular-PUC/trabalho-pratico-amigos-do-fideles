package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.sistema_hospedagem.exception.DataInvalidaException;
import com.hospedagem.sistema_hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import com.hospedagem.sistema_hospedagem.model.Aluguel;
import com.hospedagem.sistema_hospedagem.model.Cliente;
import com.hospedagem.sistema_hospedagem.model.EventoReserva;
import com.hospedagem.sistema_hospedagem.model.Notificacao;
import com.hospedagem.sistema_hospedagem.model.Quarto;
import com.hospedagem.sistema_hospedagem.model.QuartoFamilia;
import com.hospedagem.sistema_hospedagem.model.QuartoIndividual;
import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import com.hospedagem.sistema_hospedagem.notificacao.CentralDeNotificacoes;
import com.hospedagem.sistema_hospedagem.repository.AluguelRepository;
import com.hospedagem.sistema_hospedagem.repository.ClienteRepository;
import com.hospedagem.sistema_hospedagem.repository.QuartoRepository;
import com.hospedagem.sistema_hospedagem.tarifa.TarifaStrategy;
import com.hospedagem.sistema_hospedagem.tarifa.TarifaStrategyFactory;
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

    @Autowired
    private TarifaStrategyFactory tarifaStrategyFactory;

    @Autowired
    private CentralDeNotificacoes centralDeNotificacoes;

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

    public List<Aluguel> listarHistoricoCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId));
        return aluguelRepository.findByClienteId(clienteId);
    }

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
                .filter(Aluguel::isAtivo)
                .toList();

        if (!quarto.confirmarAluguel(dataEntrada, dataSaida, alugueis)) {
            throw new QuartoIndisponivelException(quartoId);
        }
    }

    public Aluguel criar(Aluguel aluguel) {
        validarDatas(aluguel.getDataEntrada(), aluguel.getDataSaida());

        Cliente cliente = clienteRepository.findById(aluguel.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + aluguel.getCliente().getId()));

        Quarto quarto = quartoRepository.findById(aluguel.getQuarto().getId())
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com id: " + aluguel.getQuarto().getId()));

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);

        verificarDisponibilidade(quarto.getId(), aluguel.getDataEntrada(), aluguel.getDataSaida());

        if (quarto instanceof QuartoFamilia qf) {
            if (aluguel.getQtdHospedes() != null && aluguel.getQtdHospedes() > qf.getCapacidadeMaxima()) {
                throw new CapacidadeExcedidaException(aluguel.getQtdHospedes(), qf.getCapacidadeMaxima());
            }
        } else if (quarto instanceof QuartoIndividual qi) {
            if (aluguel.getQtdHospedes() != null && aluguel.getQtdHospedes() > qi.getLimiteHospedes()) {
                throw new CapacidadeExcedidaException(aluguel.getQtdHospedes(), qi.getLimiteHospedes());
            }
        }

        aluguel.calcularValorFinal();

        aplicarTarifa(aluguel);

        Aluguel salvo = aluguelRepository.save(aluguel);

        notificar(EventoReserva.RESERVA_CRIADA,
                "Sua reserva (id=" + salvo.getId() + ") foi criada. Valor: R$ "
                        + String.format("%.2f", salvo.getValorFinal()),
                salvo);

        RegistroDeLogs.getInstance().info("Reserva criada id=" + salvo.getId()
                + " tarifa=" + salvo.getTipoTarifa() + " valor=" + salvo.getValorFinal());
        return salvo;
    }

    private void aplicarTarifa(Aluguel aluguel) {
        if (tarifaStrategyFactory == null) {
            return;
        }
        TipoTarifa tipo = definirTipoTarifa(aluguel);
        TarifaStrategy estrategia = tarifaStrategyFactory.resolver(tipo);
        if (estrategia == null) {
            return;
        }
        double bruto = aluguel.getValorFinal() != null ? aluguel.getValorFinal() : 0.0;
        double ajustado = estrategia.aplicar(bruto);
        aluguel.setTipoTarifa(tipo);
        aluguel.setValorFinal(ajustado);
        RegistroDeLogs.getInstance().info("Tarifa aplicada: " + estrategia.getDescricao()
                + " | bruto=" + String.format("%.2f", bruto)
                + " ajustado=" + String.format("%.2f", ajustado));
    }

    private TipoTarifa definirTipoTarifa(Aluguel aluguel) {
        TipoTarifa escolhido = aluguel.getTipoTarifa();
        if (escolhido != null && escolhido != TipoTarifa.PADRAO) {
            return escolhido;
        }
        Long clienteId = (aluguel.getCliente() != null) ? aluguel.getCliente().getId() : null;
        if (clienteId != null) {
            long ativos = aluguelRepository.findByClienteId(clienteId).stream()
                    .filter(Aluguel::isAtivo).count();
            if (ativos >= 3) {
                return TipoTarifa.CLIENTE_FREQUENTE;
            }
        }
        return TipoTarifa.PADRAO;
    }

    private void notificar(EventoReserva evento, String mensagem, Aluguel aluguel) {
        if (centralDeNotificacoes == null) {
            return;
        }
        String destinatario = (aluguel.getCliente() != null && aluguel.getCliente().getEmail() != null)
                ? aluguel.getCliente().getEmail()
                : "desconhecido";
        centralDeNotificacoes.notificar(new Notificacao(evento, mensagem, destinatario));
    }

    public Aluguel cancelar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado com id: " + id));

        if (!aluguel.isAtivo()) {
            throw new IllegalArgumentException("Aluguel já está cancelado.");
        }

        aluguel.cancelar();
        Aluguel salvo = aluguelRepository.save(aluguel);

        notificar(EventoReserva.RESERVA_CANCELADA,
                "Sua reserva (id=" + salvo.getId() + ") foi cancelada.", salvo);

        RegistroDeLogs.getInstance().alerta("Reserva cancelada id=" + salvo.getId());
        return salvo;
    }

    public void deletar(Long id) {
        aluguelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado com id: " + id));
        aluguelRepository.deleteById(id);
    }
}
