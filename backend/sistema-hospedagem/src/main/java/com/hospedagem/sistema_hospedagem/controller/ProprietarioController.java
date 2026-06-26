package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.model.Proprietario;
import com.hospedagem.sistema_hospedagem.service.ProprietarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/proprietarios")
@CrossOrigin(origins = "*")
public class ProprietarioController {

    @Autowired
    private ProprietarioService proprietarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody Proprietario proprietario,
            HttpServletRequest request) {
        try {
            Proprietario salvo = proprietarioService.cadastrar(proprietario);
            HttpSession session = request.getSession(true);
            session.setAttribute("proprietarioId", salvo.getId());
            session.setAttribute("proprietarioNome", salvo.getNome());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", salvo.getId(),
                    "nome", salvo.getNome(),
                    "email", salvo.getEmail(),
                    "tipo", "proprietario"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            Proprietario proprietario = proprietarioService.login(
                    body.get("email"), body.get("senha"));
            HttpSession session = request.getSession(true);
            session.setAttribute("proprietarioId", proprietario.getId());
            session.setAttribute("proprietarioNome", proprietario.getNome());
            return ResponseEntity.ok(Map.of(
                    "id", proprietario.getId(),
                    "nome", proprietario.getNome(),
                    "email", proprietario.getEmail(),
                    "tipo", "proprietario"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}