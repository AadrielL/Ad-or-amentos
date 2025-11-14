// MaterialResponseDTO.java
package com.adcomandos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialResponseDTO {
    private Long id;
    private String nome;
    private BigDecimal custoUnitario;
    private String unidadeMedida;
}