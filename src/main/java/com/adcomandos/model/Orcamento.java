package com.adcomandos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamentos")
@Data
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteNome;

    private String clienteTelefone;

    private String clienteEndereco;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    private BigDecimal valorMaterial;

    @Column(nullable = false)
    private BigDecimal valorMaoObra;

    private BigDecimal valorDeslocamento;

    private BigDecimal valorAjudante;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    private String complexidade;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}