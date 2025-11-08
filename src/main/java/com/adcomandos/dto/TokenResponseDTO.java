package com.adcomandos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Construtor com todos os argumentos
@NoArgsConstructor  // Construtor sem argumentos (padrão)
public class TokenResponseDTO {

    // O token JWT gerado após o login bem-sucedido
    private String token;
}