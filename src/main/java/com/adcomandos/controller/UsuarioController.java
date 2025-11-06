package com.adcomandos.controller;

import com.adcomandos.model.Usuario;
import com.adcomandos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Apenas um exemplo para obter os dados do Admin logado

    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) {
        // Retorna o objeto Usuario autenticado, ideal para painel de Admin.
        // A senha deve ser omitida ou tratada no DTO em um projeto final.
        return ResponseEntity.ok(usuarioLogado);
    }

    // TODO: Adicionar PUT para atualizar dados do Admin
}