package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.model.Quarto;
import com.hospedagem.sistema_hospedagem.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuartoService {

    @Autowired
    private QuartoRepository quartoRepository;

    public List<Quarto> listarTodos() {
        return quartoRepository.findAll();
    }

    public List<Quarto> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    public Optional<Quarto> buscarPorId(Long id) {
        return quartoRepository.findById(id);
    }

    public Quarto salvar(Quarto quarto) {
        return quartoRepository.save(quarto);
    }

    public Quarto atualizar(Long id, Quarto dados) {
        Quarto existente = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto nao encontrado com id: " + id));
        existente.setValorBase(dados.getValorBase());
        existente.setPossuiAR(dados.getPossuiAR());
        existente.setPossuiHidro(dados.getPossuiHidro());
        return quartoRepository.save(existente);
    }

    public void deletar(Long id) {
        quartoRepository.deleteById(id);
    }
}
