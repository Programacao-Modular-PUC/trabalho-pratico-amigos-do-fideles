package com.hospedagem.sistema_hospedagem.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RegistroDeLogs {

    public enum Nivel { INFO, ALERTA, ERRO }

    private RegistroDeLogs() {
    }

    private static class Holder {
        private static final RegistroDeLogs INSTANCIA = new RegistroDeLogs();
    }

    public static RegistroDeLogs getInstance() {
        return Holder.INSTANCIA;
    }

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<String> registros = new CopyOnWriteArrayList<>();

    public void registrar(Nivel nivel, String mensagem) {
        String linha = String.format("[%s] %-6s %s",
                LocalDateTime.now().format(FORMATO), nivel, mensagem);
        registros.add(linha);
        System.out.println("LOG " + linha);
    }

    public void info(String mensagem) {
        registrar(Nivel.INFO, mensagem);
    }

    public void alerta(String mensagem) {
        registrar(Nivel.ALERTA, mensagem);
    }

    public void erro(String mensagem) {
        registrar(Nivel.ERRO, mensagem);
    }

    public List<String> listar() {
        return Collections.unmodifiableList(registros);
    }

    public void limpar() {
        registros.clear();
    }
}