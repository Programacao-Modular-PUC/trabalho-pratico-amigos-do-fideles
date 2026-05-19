package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.model.Residencia;
import com.hospedagem.sistema_hospedagem.service.ResidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/residencias")
@CrossOrigin(origins = "*")
public class ResidenciaController {

    @Autowired
    private ResidenciaService residenciaService;

    @GetMapping
    public ResponseEntity<List<Residencia>> listarTodas() {
        return ResponseEntity.ok(residenciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Residencia> buscarPorId(@PathVariable Long id) {
        return residenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Residencia> criar(@RequestBody Residencia residencia) {
        Residencia salva = residenciaService.salvar(residencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Residencia> atualizar(@PathVariable Long id, @RequestBody Residencia residencia) {
        try {
            Residencia atualizada = residenciaService.atualizar(id, residencia);
            return ResponseEntity.ok(atualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        residenciaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
