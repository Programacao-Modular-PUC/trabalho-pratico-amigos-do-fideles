package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.model.*;
import com.hospedagem.sistema_hospedagem.repository.ResidenciaRepository;
import com.hospedagem.sistema_hospedagem.service.QuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quartos")
@CrossOrigin(origins = "*")
public class QuartoController {

    @Autowired
    private QuartoService quartoService;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    /**
     * Lista todos os quartos.
     * Filtros opcionais: ?residenciaId={id} ou ?tipo=individual|duplo|familia  (Sprint 3)
     */
    @GetMapping
    public ResponseEntity<List<Quarto>> listarTodos(
            @RequestParam(required = false) Long residenciaId,
            @RequestParam(required = false) String tipo) {

        if (residenciaId != null) return ResponseEntity.ok(quartoService.listarPorResidencia(residenciaId));
        if (tipo != null)         return ResponseEntity.ok(quartoService.listarPorTipo(tipo));
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

    @PostMapping("/individual")
    public ResponseEntity<?> criarIndividual(@RequestBody Map<String, Object> body) {
        // Validação: berço não é permitido em quarto individual (Sprint 3)
        boolean solicitouBerco = Boolean.parseBoolean(
                body.getOrDefault("possuiBerco", "false").toString());
        quartoService.validarBercoEmIndividual(solicitouBerco); // lança RecursoNaoPermitidoException

        Residencia residencia = residenciaRepository
                .findById(Long.valueOf(body.get("residenciaId").toString()))
                .orElseThrow(() -> new RuntimeException("Residência não encontrada"));

        QuartoIndividual quarto = new QuartoIndividual(
                Double.valueOf(body.get("valorBase").toString()),
                Boolean.valueOf(body.get("possuiAR").toString()),
                Boolean.valueOf(body.get("possuiHidro").toString()),
                residencia,
                Integer.valueOf(body.get("quantidadeCamas").toString()),
                Double.valueOf(body.get("valorAdicionalPorCama").toString()));

        return ResponseEntity.status(HttpStatus.CREATED).body(quartoService.salvar(quarto));
    }

    @PostMapping("/duplo")
    public ResponseEntity<?> criarDuplo(@RequestBody Map<String, Object> body) {
        Residencia residencia = residenciaRepository
                .findById(Long.valueOf(body.get("residenciaId").toString()))
                .orElseThrow(() -> new RuntimeException("Residência não encontrada"));

        QuartoDuplo quarto = new QuartoDuplo(
                Double.valueOf(body.get("valorBase").toString()),
                Boolean.valueOf(body.get("possuiAR").toString()),
                Boolean.valueOf(body.get("possuiHidro").toString()),
                residencia,
                body.get("tipoCama").toString(),
                Boolean.valueOf(body.get("possuiBerco").toString()),
                Double.valueOf(body.get("taxaBerco").toString()),
                Double.valueOf(body.get("adicionalConforto").toString()));

        return ResponseEntity.status(HttpStatus.CREATED).body(quartoService.salvar(quarto));
    }

    @PostMapping("/familia")
    public ResponseEntity<?> criarFamilia(@RequestBody Map<String, Object> body) {
        Residencia residencia = residenciaRepository
                .findById(Long.valueOf(body.get("residenciaId").toString()))
                .orElseThrow(() -> new RuntimeException("Residência não encontrada"));

        QuartoFamilia quarto = new QuartoFamilia(
                Double.valueOf(body.get("valorBase").toString()),
                Boolean.valueOf(body.get("possuiAR").toString()),
                Boolean.valueOf(body.get("possuiHidro").toString()),
                residencia,
                Integer.valueOf(body.get("capacidadeMaxima").toString()),
                Integer.valueOf(body.get("quantidadeAmbientes").toString()),
                Double.valueOf(body.get("valorPorHospede").toString()),
                Double.valueOf(body.get("percentualAdicional").toString()));

        return ResponseEntity.status(HttpStatus.CREATED).body(quartoService.salvar(quarto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @RequestBody Quarto quarto) {
        return ResponseEntity.ok(quartoService.atualizar(id, quarto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        quartoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
