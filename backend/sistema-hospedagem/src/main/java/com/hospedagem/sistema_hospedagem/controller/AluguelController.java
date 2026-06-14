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

        if (clienteId != null) return ResponseEntity.ok(aluguelService.listarPorCliente(clienteId));
        if (quartoId  != null) return ResponseEntity.ok(aluguelService.listarPorQuarto(quartoId));
        return ResponseEntity.ok(aluguelService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscarPorId(@PathVariable Long id) {
        return aluguelService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Histórico completo (ativo + cancelado) de um cliente. Sprint 3. */
    @GetMapping("/historico/{clienteId}")
    public ResponseEntity<List<Aluguel>> historico(@PathVariable Long clienteId) {
        return ResponseEntity.ok(aluguelService.listarHistoricoCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<Aluguel> criar(@RequestBody Aluguel aluguel) {
        // Exceções tratadas pelo GlobalExceptionHandler
        Aluguel criado = aluguelService.criar(aluguel);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    /** Cancelamento de aluguel. Sprint 3. */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Aluguel> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        aluguelService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
