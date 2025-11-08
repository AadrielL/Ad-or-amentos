package com.adcomandos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServicoPadraoRequestDTO {

    @NotBlank(message = "O nome do serviço é obrigatório")
    private String nome;

    @NotBlank(message = "O tipo/categoria do serviço é obrigatório (ex: CFTV, Elétrica)")
    private String tipo;

    @NotNull(message = "O valor base do serviço é obrigatório")
    @Positive(message = "O valor base deve ser maior que zero")
    private BigDecimal valorBase;

    @NotNull(message = "O tempo estimado em horas é obrigatório")
    @Positive(message = "O tempo estimado deve ser maior que zero")
    private Double tempoEstimadoHoras;
}