package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.model.Aluguel;
import com.hospedagem.sistema_hospedagem.service.AluguelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alugueis")
@CrossOrigin(origins = "*")
public class AluguelController {

    @Autowired
    private AluguelService aluguelService;

    @GetMapping
    public ResponseEntity<List<Aluguel>> listarTodos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long quartoId) {

        if (clienteId != null) {
            return ResponseEntity.ok(aluguelService.listarPorCliente(clienteId));
        }
        if (quartoId != null) {
            return ResponseEntity.ok(aluguelService.listarPorQuarto(quartoId));
        }
        return ResponseEntity.ok(aluguelService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscarPorId(@PathVariable Long id) {
        return aluguelService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Aluguel> criar(@RequestBody Aluguel aluguel) {
        try {
            Aluguel criado = aluguelService.criar(aluguel);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        aluguelService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
