package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.dto.AuthResponse;
import com.hospedagem.sistema_hospedagem.dto.CadastroRequest;
import com.hospedagem.sistema_hospedagem.dto.LoginRequest;
import com.hospedagem.sistema_hospedagem.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:3000"},
             allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            AuthResponse response = authService.cadastrar(request, httpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            AuthResponse response = authService.login(request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.ok(Map.of("mensagem", "Logout realizado com sucesso."));
    }

    // Retorna dados do usuario logado com base na sessao ativa
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Nenhuma sessao ativa."));
        }
        return ResponseEntity.ok(Map.of(
                "clienteId", session.getAttribute("clienteId"),
                "nome", session.getAttribute("clienteNome")
        ));
    }
}
