package com.hospedagem.sistema_hospedagem.service;

import com.hospedagem.sistema_hospedagem.dto.AuthResponse;
import com.hospedagem.sistema_hospedagem.dto.CadastroRequest;
import com.hospedagem.sistema_hospedagem.dto.LoginRequest;
import com.hospedagem.sistema_hospedagem.model.Cliente;
import com.hospedagem.sistema_hospedagem.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse cadastrar(CadastroRequest request, HttpServletRequest httpRequest) {
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-mail ja cadastrado.");
        }
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF ja cadastrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setEndereco(request.getEndereco());
        cliente.setTelefone(request.getTelefone());
        cliente.setEmail(request.getEmail());
        cliente.setSenha(passwordEncoder.encode(request.getSenha()));
        clienteRepository.save(cliente);

        criarSessao(cliente, httpRequest);
        return new AuthResponse(cliente.getNome(), cliente.getEmail(), cliente.getId());
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha invalidos."));

        if (!passwordEncoder.matches(request.getSenha(), cliente.getSenha())) {
            throw new RuntimeException("E-mail ou senha invalidos.");
        }

        criarSessao(cliente, httpRequest);
        return new AuthResponse(cliente.getNome(), cliente.getEmail(), cliente.getId());
    }

    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    private void criarSessao(Cliente cliente, HttpServletRequest httpRequest) {
        HttpSession sessionAntiga = httpRequest.getSession(false);
        if (sessionAntiga != null) sessionAntiga.invalidate();

        HttpSession session = httpRequest.getSession(true);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                cliente.getEmail(), null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        session.setAttribute("clienteId", cliente.getId());
        session.setAttribute("clienteNome", cliente.getNome());
    }
}
