package com.adcomandos.controller;

import com.adcomandos.dto.LevantamentoRequestDTO; // Usando o novo DTO
import com.adcomandos.dto.OrcamentoResponseDTO;
import com.adcomandos.service.LevantamentoMaterialService; // Usando o novo Service
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/levantamento") // NOVO ENDPOINT!
public class LevantamentoController {

    private final LevantamentoMaterialService levantamentoMaterialService;

    public LevantamentoController(LevantamentoMaterialService levantamentoMaterialService) {
        this.levantamentoMaterialService = levantamentoMaterialService;
    }

    /**
     * POST: Gera o levantamento técnico de material. Rota exclusiva para Admins.
     */
    @PostMapping
    public ResponseEntity<OrcamentoResponseDTO> gerarLevantamento(
            @RequestBody @Valid  LevantamentoRequestDTO request,
            Authentication authentication) {

        // 1. O Controller deve garantir que o usuário é um Admin.
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            // Se não estiver logado, proíbe o acesso ao levantamento técnico.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 2. É um Admin logado.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailAdmin = userDetails.getUsername();

        // Chamada ao novo serviço de levantamento
        OrcamentoResponseDTO response = levantamentoMaterialService.levantarMaterial(request, emailAdmin);

        // Retorna 200 OK
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}