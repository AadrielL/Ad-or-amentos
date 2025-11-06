package com.adcomandos.repository;

import com.adcomandos.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    // Métodos personalizados aqui
}