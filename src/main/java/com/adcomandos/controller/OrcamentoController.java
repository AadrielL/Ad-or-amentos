package com.adcomandos.controller;

import com.adcomandos.dto.OrcamentoRequestDTO;
import com.adcomandos.dto.OrcamentoResponseDTO;
import com.adcomandos.model.Usuario;
import com.adcomandos.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orcamentos")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    // Endpoint: /api/orcamentos (Requer autenticação de ADMIN)
    @PostMapping
    public ResponseEntity<OrcamentoResponseDTO> calcularOrcamento(
            @RequestBody @Valid OrcamentoRequestDTO request,
            // Spring Security injeta o objeto Usuario logado
            @AuthenticationPrincipal Usuario usuarioLogado) {

        try {
            // Chamada ao serviço de cálculo, passando o usuário para garantir o isolamento
            OrcamentoResponseDTO response = orcamentoService.calcularOrcamento(request, usuarioLogado);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    OrcamentoResponseDTO.builder().mensagem("Erro no cálculo: " + e.getMessage()).build()
            );
        } catch (SecurityException e) {
            // Erro se tentar usar o preço de outro usuário
            return ResponseEntity.status(403).body(
                    OrcamentoResponseDTO.builder().mensagem("Acesso negado: " + e.getMessage()).build()
            );
        }
    }

    // TODO: Adicionar endpoints GET para listar orçamentos, GET/{id} para detalhar, etc.
}