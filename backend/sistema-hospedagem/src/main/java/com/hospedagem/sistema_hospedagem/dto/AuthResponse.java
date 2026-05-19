package com.hospedagem.sistema_hospedagem.dto;

public class AuthResponse {
    private String nome;
    private String email;
    private Long clienteId;

    public AuthResponse(String nome, String email, Long clienteId) {
        this.nome = nome;
        this.email = email;
        this.clienteId = clienteId;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Long getClienteId() { return clienteId; }
}
