package com.adcomandos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos_padrao")
@Data
public class ServicoPadrao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String tipo; // Ex: CFTV, Elétrica, Hidráulica

    @Column(nullable = false)
    private BigDecimal valorBase; // Custo base da hora ou do serviço

    @Column(nullable = false)
    private Double tempoEstimadoHoras; // Tempo em horas para o cálculo

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Chave estrangeira para o usuário que cadastrou o serviço
}