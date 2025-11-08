package com.adcomandos.controller;

import com.adcomandos.dto.ServicoPadraoRequestDTO;
import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import com.adcomandos.service.ServicoPadraoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoPadraoController {

    private final ServicoPadraoService servicoPadraoService;

    public ServicoPadraoController(ServicoPadraoService servicoPadraoService) {
        this.servicoPadraoService = servicoPadraoService;
    }

    /**
     * POST: Cadastrar novo serviço padrão (apenas ADMIN).
     * Rota: /api/servicos
     */
    @PostMapping
    public ResponseEntity<ServicoPadrao> createServico(
            @RequestBody @Valid ServicoPadraoRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        ServicoPadrao novoServico = servicoPadraoService.create(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
    }

    /**
     * GET: Listar todos os serviços do usuário logado (apenas ADMIN).
     * Rota: /api/servicos
     */
    @GetMapping
    public ResponseEntity<List<ServicoPadrao>> getAllServicos(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        List<ServicoPadrao> servicos = servicoPadraoService.findAllByUsuario(usuarioLogado);
        return ResponseEntity.ok(servicos);
    }

    /**
     * PUT /{id}: Atualizar um serviço padrão existente (apenas ADMIN).
     * Rota: /api/servicos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServicoPadrao> updateServico(
            @PathVariable Long id,
            @RequestBody @Valid ServicoPadraoRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        try {
            ServicoPadrao servicoAtualizado = servicoPadraoService.update(id, dto, usuarioLogado);
            return ResponseEntity.ok(servicoAtualizado);
        } catch (SecurityException e) {
            // Retorna 403 se o usuário tentar alterar um serviço que não é dele.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            // Retorna 404 se o ID do serviço não for encontrado.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * DELETE /{id}: Deletar um serviço (apenas ADMIN).
     * Rota: /api/servicos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServico(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        try {
            servicoPadraoService.delete(id, usuarioLogado);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}