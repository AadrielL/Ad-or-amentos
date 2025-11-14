package com.adcomandos.controller;

import com.adcomandos.dto.LevantamentoRequestDTO;
import com.adcomandos.dto.OrcamentoResponseDTO;
import com.adcomandos.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por gerar o orçamento final para o cliente.
 * Rota PÚBLICA, não requer autenticação.
 */
@RestController
@RequestMapping("/api/orcamento") // Rota clara para o cliente
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    /**
     * POST: Gera o orçamento completo (material + mão de obra) e persiste no DB.
     * Rota de uso público/cliente.
     */
    @PostMapping // Rota: POST /api/orcamento
    public ResponseEntity<OrcamentoResponseDTO> gerarOrcamentoParaCliente(
            @RequestBody @Valid LevantamentoRequestDTO request) {

        // CORREÇÃO: Chama o método 'calcularOrcamentoParaCliente', que é o método
        // público existente no Service e que já faz a persistência interna.
        OrcamentoResponseDTO response = orcamentoService.calcularOrcamentoParaCliente(request);

        // Retorna 201 Created (pois cria um registro de orçamento no DB)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}