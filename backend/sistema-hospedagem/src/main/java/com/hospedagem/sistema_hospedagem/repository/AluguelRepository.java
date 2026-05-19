package com.hospedagem.sistema_hospedagem.repository;

import com.hospedagem.sistema_hospedagem.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    List<Aluguel> findByClienteId(Long clienteId);
    List<Aluguel> findByQuartoId(Long quartoId);
}
