package com.adcomandos.model;



import jakarta.persistence.*;

import lombok.Data;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;



@Entity

@Table(name = "orcamentos")

@Data

public class Orcamento {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



// --- Dados do Cliente ---

    @Column(name = "cliente_nome", nullable = true)

    private String clienteNome;



    private String clienteTelefone;



    private String endereco;



    @Column(name = "data_criacao", nullable = false)

    private LocalDateTime dataGeracao;



// --- Dados de Cálculo (Entrada) ---

    @Column(nullable = false)

    private Double metragemQuadrada;



    @Column(nullable = false)

    private Double tempoEstimadoHoras;



// --- Valores de Serviço e Custo (Obrigatórios no DB) ---



// Assume-se que esta é a coluna de custo de mão de obra

    @Column(name = "valor_mao_obra", nullable = false)

    private BigDecimal custoTotalMaoDeObra;



    private BigDecimal valorDeslocamento;



    private BigDecimal valorAjudante;



    @Column(nullable = false)

    private Integer quantidadeTomadas;



    @Column(nullable = false)

    private Integer quantidadePontosLuz;



// Assume-se que esta é a coluna de valor total (evitando o conflito de nomes)

    @Column(name = "valor_total_servico", nullable = false)

    private BigDecimal valorTotalServico;



    private String nivelComplexidade;



    @ManyToOne

    @JoinColumn(name = "usuario_id", nullable = true)

    private Usuario usuario;



// --- Relacionamento de Normalização (CORRIGIDO!) ---

// Mapeia a lista de itens de material deste orçamento.

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<ItemMaterial> itensMaterial; // O tipo correto é List<ItemMaterial>





    @PrePersist

    protected void onCreate() {

        if (this.dataGeracao == null) {

            this.dataGeracao = LocalDateTime.now();

        }

    }

}