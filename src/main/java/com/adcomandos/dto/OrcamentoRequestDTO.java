package com.adcomandos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrcamentoRequestDTO {

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String clienteNome;

    private String clienteTelefone;

    private String clienteEndereco;

    @NotNull(message = "A complexidade é obrigatória")
    private String complexidade;

    private Double horasDeslocamento;

    private Integer horasAjudante;

    @NotNull(message = "A lista de itens do orçamento não pode ser nula")
    private List<ItemOrcamentoDTO> itens;

    @Data
    public static class ItemOrcamentoDTO {
        @NotNull
        private Long idReferencia;

        @NotBlank
        private String tipo; // "MATERIAL" ou "SERVICO"

        @NotNull
        private Double quantidade;
    }
}