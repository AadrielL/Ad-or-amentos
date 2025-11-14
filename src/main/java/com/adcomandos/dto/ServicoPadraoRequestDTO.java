package com.adcomandos.dto;

import com.adcomandos.model.TipoServico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO simples, apenas com os campos essenciais.
public class ServicoPadraoRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotNull(message = "O tipo de serviço é obrigatório.")
    private TipoServico tipo;

    @NotNull(message = "O valor base é obrigatório.")
    private Double valorBase;

    @NotNull(message = "O tempo estimado em horas é obrigatório.")
    private Double tempoEstimadoHoras;

    // --- Getters e Setters ---

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoServico getTipo() { return tipo; }
    public void setTipo(TipoServico tipo) { this.tipo = tipo; }
    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }
    public Double getTempoEstimadoHoras() { return tempoEstimadoHoras; }
    public void setTempoEstimadoHoras(Double tempoEstimadoHoras) { this.tempoEstimadoHoras = tempoEstimadoHoras; }
}