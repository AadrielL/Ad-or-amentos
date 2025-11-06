package com.adcomandos.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrcamentoResponseDTO {

    private String clienteNome;
    private BigDecimal valorMaterial;
    private BigDecimal valorMaoObra;
    private BigDecimal valorDeslocamento;
    private BigDecimal valorAjudante;
    private BigDecimal valorTotal;
    private String complexidadeAplicada;
    private String mensagem;
}