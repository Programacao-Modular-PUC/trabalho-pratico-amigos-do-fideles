package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.model.Proprietario;
import com.hospedagem.sistema_hospedagem.model.Residencia;
import com.hospedagem.sistema_hospedagem.repository.ProprietarioRepository;
import com.hospedagem.sistema_hospedagem.repository.ResidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ResidenciaService {

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    public List<Residencia> listarTodas() {
        return residenciaRepository.findAll();
    }

    public Optional<Residencia> buscarPorId(Long id) {
        return residenciaRepository.findById(id);
    }

    public Residencia salvar(Residencia residencia) {
        return residenciaRepository.save(residencia);
    }

    public Residencia salvarComProprietario(Residencia residencia, Long proprietarioId) {
        Proprietario proprietario = proprietarioRepository.findById(proprietarioId)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));
        proprietario.cadastrarResidencia(residencia);
        return residenciaRepository.save(residencia);
    }

    public Residencia atualizar(Long id, Residencia residenciaAtualizada) {
        Residencia residencia = residenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Residencia não encontrada"));
        residencia.setEndereco(residenciaAtualizada.getEndereco());
        residencia.setNumero(residenciaAtualizada.getNumero());
        residencia.setBairro(residenciaAtualizada.getBairro());
        residencia.setCep(residenciaAtualizada.getCep());
        residencia.setTelefone(residenciaAtualizada.getTelefone());
        residencia.setEmail(residenciaAtualizada.getEmail());
        residencia.setFotoUrl(residenciaAtualizada.getFotoUrl());
        return residenciaRepository.save(residencia);
    }

    public void deletar(Long id) {
        residenciaRepository.deleteById(id);
    }
}