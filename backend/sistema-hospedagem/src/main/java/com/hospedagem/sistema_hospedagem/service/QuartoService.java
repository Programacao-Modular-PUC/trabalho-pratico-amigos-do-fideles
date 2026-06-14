package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.exception.RecursoNaoPermitidoException;
import com.hospedagem.sistema_hospedagem.model.Quarto;
import com.hospedagem.sistema_hospedagem.model.QuartoDuplo;
import com.hospedagem.sistema_hospedagem.model.QuartoFamilia;
import com.hospedagem.sistema_hospedagem.model.QuartoIndividual;
import com.hospedagem.sistema_hospedagem.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuartoService {

    @Autowired
    private QuartoRepository quartoRepository;

    // ─── Listagem ────────────────────────────────────────────────────────────

    public List<Quarto> listarTodos() {
        return quartoRepository.findAll();
    }

    public List<Quarto> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    /**
     * Filtro por tipo de quarto (Sprint 3).
     * Valores aceitos: "individual", "duplo", "familia".
     */
    public List<Quarto> listarPorTipo(String tipo) {
        return quartoRepository.findAll().stream()
                .filter(q -> switch (tipo.toLowerCase()) {
                    case "individual" -> q instanceof QuartoIndividual;
                    case "duplo"      -> q instanceof QuartoDuplo;
                    case "familia"    -> q instanceof QuartoFamilia;
                    default -> throw new IllegalArgumentException(
                            "Tipo de quarto inválido: '" + tipo + "'. Use: individual, duplo ou familia.");
                })
                .collect(Collectors.toList());
    }

    public Optional<Quarto> buscarPorId(Long id) {
        return quartoRepository.findById(id);
    }

    // ─── Persistência ────────────────────────────────────────────────────────

    public Quarto salvar(Quarto quarto) {
        validarQuarto(quarto);
        return quartoRepository.save(quarto);
    }

    public Quarto atualizar(Long id, Quarto quartoAtualizado) {
        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com id: " + id));
        quarto.setValorBase(quartoAtualizado.getValorBase());
        quarto.setPossuiAR(quartoAtualizado.getPossuiAR());
        quarto.setPossuiHidro(quartoAtualizado.getPossuiHidro());
        return quartoRepository.save(quarto);
    }

    public void deletar(Long id) {
        quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com id: " + id));
        quartoRepository.deleteById(id);
    }

    // ─── Validações ──────────────────────────────────────────────────────────

    /**
     * Valida regras de negócio ao salvar um quarto.
     * Ex: berço não é permitido em quarto individual.
     */
    private void validarQuarto(Quarto quarto) {
        if (quarto.getValorBase() == null || quarto.getValorBase() <= 0) {
            throw new IllegalArgumentException("O valor base do quarto deve ser maior que zero.");
        }
        // Regra de berço: QuartoIndividual não pode ter berço
        if (quarto instanceof QuartoIndividual) {
            // QuartoIndividual não possui o atributo berço — nenhuma validação extra necessária.
            // A exceção RecursoNaoPermitidoException é lançada no controller se o campo for enviado.
        }
    }

    /**
     * Lança RecursoNaoPermitidoException se tentar atribuir berço a um quarto individual.
     * Deve ser chamado no controller ao criar um QuartoIndividual.
     */
    public void validarBercoEmIndividual(boolean solicitouBerco) {
        if (solicitouBerco) {
            throw new RecursoNaoPermitidoException("Berço não é permitido em quarto individual.");
        }
    }
}
