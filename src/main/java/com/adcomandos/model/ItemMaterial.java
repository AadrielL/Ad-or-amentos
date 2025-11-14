package com.adcomandos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_material")
public class ItemMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com a Entity Orcamento
    @ManyToOne
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @Column(name = "nome_material", nullable = false)
    private String nomeMaterial;

    @Column(name = "quantidade_necessaria", nullable = false)
    private Double quantidadeNecessaria;

    @Column(name = "unidade_medida", nullable = false)
    private String unidadeMedida;

    @Column(name = "custo_unitario_referencia", nullable = false)
    private BigDecimal custoUnitarioReferencia;

    // Construtor padrão exigido pelo JPA
    public ItemMaterial() {}

    /**
     * Construtor utilizado pelo ItemMaterialService para criar um novo item.
     */
    public ItemMaterial(String nomeMaterial, Double quantidadeNecessaria, String unidadeMedida) {
        this.nomeMaterial = nomeMaterial;
        this.quantidadeNecessaria = quantidadeNecessaria;
        this.unidadeMedida = unidadeMedida;
        this.custoUnitarioReferencia = BigDecimal.ZERO; // Inicialização obrigatória
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }
    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public String getNomeMaterial() {
        return nomeMaterial;
    }
    public void setNomeMaterial(String nomeMaterial) {
        this.nomeMaterial = nomeMaterial;
    }

    public Double getQuantidadeNecessaria() {
        return quantidadeNecessaria;
    }
    public void setQuantidadeNecessaria(Double quantidadeNecessaria) {
        this.quantidadeNecessaria = quantidadeNecessaria;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }
    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getCustoUnitarioReferencia() {
        return custoUnitarioReferencia;
    }
    public void setCustoUnitarioReferencia(BigDecimal custoUnitarioReferencia) {
        this.custoUnitarioReferencia = custoUnitarioReferencia;
    }
}