package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.model.Quarto;
import com.hospedagem.sistema_hospedagem.service.QuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quartos")
@CrossOrigin(origins = "*")
public class QuartoController {

    @Autowired
    private QuartoService quartoService;

    @GetMapping
    public ResponseEntity<List<Quarto>> listarTodos(
            @RequestParam(required = false) Long residenciaId) {

        if (residenciaId != null) {
            return ResponseEntity.ok(quartoService.listarPorResidencia(residenciaId));
        }
        return ResponseEntity.ok(quartoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        return quartoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/diaria")
    public ResponseEntity<Double> calcularDiaria(@PathVariable Long id) {
        return quartoService.buscarPorId(id)
                .map(q -> ResponseEntity.ok(q.calcularDiaria()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Quarto> criar(@RequestBody Quarto quarto) {
        Quarto salvo = quartoService.salvar(quarto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @RequestBody Quarto quarto) {
        try {
            Quarto atualizado = quartoService.atualizar(id, quarto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        quartoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
