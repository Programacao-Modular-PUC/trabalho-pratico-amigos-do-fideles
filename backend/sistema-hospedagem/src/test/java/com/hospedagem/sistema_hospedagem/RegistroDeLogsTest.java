package com.hospedagem.sistema_hospedagem;

import com.hospedagem.sistema_hospedagem.log.RegistroDeLogs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistroDeLogsTest {

    @Test
    @DisplayName("getInstance retorna sempre a mesma instância")
    void getInstance_RetornaMesmaInstancia() {
        RegistroDeLogs a = RegistroDeLogs.getInstance();
        RegistroDeLogs b = RegistroDeLogs.getInstance();
        assertSame(a, b);
    }

    @Test
    @DisplayName("registrar adiciona entrada ao histórico compartilhado")
    void registrar_AdicionaAoHistorico() {
        RegistroDeLogs.getInstance().limpar();
        RegistroDeLogs.getInstance().info("teste-de-log");
        boolean encontrou = RegistroDeLogs.getInstance().listar().stream()
                .anyMatch(l -> l.contains("teste-de-log"));
        assertTrue(encontrou);
    }
}