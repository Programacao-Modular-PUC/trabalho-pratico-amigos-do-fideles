package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.model.Proprietario;
import com.hospedagem.sistema_hospedagem.repository.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProprietarioService {

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Proprietario> listarTodos() {
        return proprietarioRepository.findAll();
    }

    public Optional<Proprietario> buscarPorId(Long id) {
        return proprietarioRepository.findById(id);
    }

    public Proprietario cadastrar(Proprietario proprietario) {
        if (proprietarioRepository.existsByEmail(proprietario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado.");
        }
        if (proprietarioRepository.existsByCpf(proprietario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado.");
        }
        proprietario.setSenha(passwordEncoder.encode(proprietario.getSenha()));
        return proprietarioRepository.save(proprietario);
    }

    public Proprietario login(String email, String senha) {
        Proprietario proprietario = proprietarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));
        if (!passwordEncoder.matches(senha, proprietario.getSenha())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }
        return proprietario;
    }

    public void deletar(Long id) {
        proprietarioRepository.deleteById(id);
    }
}