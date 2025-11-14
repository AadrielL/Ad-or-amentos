package com.adcomandos.model;

import jakarta.persistence.*;

// Entidade ServicoPadrao restaurada, sem relacionamento com RegraServico.
@Entity
@Table(name = "servicos_padrao")
public class ServicoPadrao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoServico tipo; // Assumindo que você tem um Enum TipoServico

    private Double valorBase;
    private Double tempoEstimadoHoras;

    // Relacionamento ManyToOne com o usuário que criou o serviço
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoServico getTipo() { return tipo; }
    public void setTipo(TipoServico tipo) { this.tipo = tipo; }
    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }
    public Double getTempoEstimadoHoras() { return tempoEstimadoHoras; }
    public void setTempoEstimadoHoras(Double tempoEstimadoHoras) { this.tempoEstimadoHoras = tempoEstimadoHoras; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}