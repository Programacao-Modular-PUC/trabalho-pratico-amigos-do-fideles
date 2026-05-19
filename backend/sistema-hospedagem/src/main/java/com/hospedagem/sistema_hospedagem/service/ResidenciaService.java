package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.model.Residencia;
import com.hospedagem.sistema_hospedagem.repository.ResidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResidenciaService {

    @Autowired
    private ResidenciaRepository residenciaRepository;

    public List<Residencia> listarTodas() {
        return residenciaRepository.findAll();
    }

    public Optional<Residencia> buscarPorId(Long id) {
        return residenciaRepository.findById(id);
    }

    public Residencia salvar(Residencia residencia) {
        return residenciaRepository.save(residencia);
    }

    public Residencia atualizar(Long id, Residencia residenciaAtualizada) {
        Residencia existente = residenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Residência não encontrada com id: " + id));

        existente.setEndereco(residenciaAtualizada.getEndereco());
        existente.setNumero(residenciaAtualizada.getNumero());
        existente.setBairro(residenciaAtualizada.getBairro());
        existente.setCep(residenciaAtualizada.getCep());
        existente.setTelefone(residenciaAtualizada.getTelefone());
        existente.setEmail(residenciaAtualizada.getEmail());

        return residenciaRepository.save(existente);
    }

    public void deletar(Long id) {
        residenciaRepository.deleteById(id);
    }
}
