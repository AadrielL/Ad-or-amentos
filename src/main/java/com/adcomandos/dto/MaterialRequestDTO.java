// MaterialRequestDTO.java Corrigido
package com.adcomandos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MaterialRequestDTO {

    @NotBlank(message = "O nome do material é obrigatório")
    private String nome; // ⬅️ CORRIGIDO: Era 'clienteNome', agora é 'nome'

    @NotNull(message = "O custo unitário é obrigatório")
    @Positive(message = "O custo unitário deve ser positivo")
    private BigDecimal custoUnitario;

    @NotBlank(message = "A unidade de medida (ex: MT, UN, KG) é obrigatória")
    private String unidadeMedida;

    // ❌ OS MÉTODOS INCOMPLETOS 'get()' E 'getNome()' FORAM REMOVIDOS.
}