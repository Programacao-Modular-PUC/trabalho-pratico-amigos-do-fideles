package com.hospedagem.sistema_hospedagem.controller;

import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import com.hospedagem.sistema_hospedagem.model.Notificacao;
import com.hospedagem.sistema_hospedagem.model.TipoTarifa;
import com.hospedagem.sistema_hospedagem.notificacao.CentralDeNotificacoes;
import com.hospedagem.sistema_hospedagem.tarifa.TarifaStrategy;
import com.hospedagem.sistema_hospedagem.tarifa.TarifaStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sistema")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SistemaController {

    @Autowired
    private CentralDeNotificacoes centralDeNotificacoes;

    @Autowired
    private TarifaStrategyFactory tarifaStrategyFactory;

    @GetMapping("/logs")
    public ResponseEntity<List<String>> logs() {
        return ResponseEntity.ok(RegistroDeLogs.getInstance().listar());
    }

    @GetMapping("/notificacoes")
    public ResponseEntity<Map<String, Object>> notificacoes() {
        List<Map<String, Object>> itens = new ArrayList<>();
        for (Notificacao n : centralDeNotificacoes.getHistorico()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("evento", n.getEvento());
            item.put("destinatario", n.getDestinatario());
            item.put("mensagem", n.getMensagem());
            item.put("momento", n.getMomento());
            itens.add(item);
        }
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("canaisAtivos", centralDeNotificacoes.getNomesDosCanais());
        resposta.put("totalEmitidas", itens.size());
        resposta.put("historico", itens);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/tarifas")
    public ResponseEntity<List<Map<String, Object>>> tarifas() {
        List<Map<String, Object>> catalogo = new ArrayList<>();
        for (Map.Entry<TipoTarifa, TarifaStrategy> e : tarifaStrategyFactory.getEstrategias().entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tipo", e.getKey());
            item.put("descricao", e.getValue().getDescricao());
            item.put("exemploSobre100", e.getValue().aplicar(100.0));
            catalogo.add(item);
        }
        return ResponseEntity.ok(catalogo);
    }
}