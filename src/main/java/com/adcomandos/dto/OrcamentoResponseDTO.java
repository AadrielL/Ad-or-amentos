package com.adcomandos.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrcamentoResponseDTO {
    private Long id;
    private String clienteNome;
    private String endereco;
    private LocalDateTime dataGeracao;
    private String adminResponsavel;
    private String nivelComplexidade;

    // Tempo Estimado
    private Double tempoEstimadoHoras;
    private String tempoEstimadoFormatado; // NOVO CAMPO: Ex: "4 dias e 5.0 horas"

    // Valores
    private BigDecimal custoTotalMaoDeObra;
    private BigDecimal custoTotalMaterial;
    private BigDecimal valorDeslocamento;
    private BigDecimal valorAjudante;
    private BigDecimal valorTotalServico;

    // Materiais
    private List<MaterialRecomendadoDTO> materiaisRecomendados;

    @Data
    @Builder
    public static class MaterialRecomendadoDTO {
        private String nome;
        private Double quantidadeNecessaria;
        private String unidadeMedida;
        private BigDecimal custoUnitarioReferencia;
    }
}