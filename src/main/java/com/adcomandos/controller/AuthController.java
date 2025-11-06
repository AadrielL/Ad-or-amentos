package com.adcomandos.controller;

import com.adcomandos.dto.AdminCreationDTO;
import com.adcomandos.dto.LoginDTO;
import com.adcomandos.model.Usuario;
import com.adcomandos.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AdminService adminService, AuthenticationManager authenticationManager) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
    }

    // Endpoint: /api/auth/register-admin
    @PostMapping("/register-admin")
    public ResponseEntity<Usuario> registerAdmin(@RequestBody @Valid AdminCreationDTO dto) {
        try {
            Usuario novoAdmin = adminService.criarPrimeiroAdmin(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
        } catch (IllegalArgumentException e) {
            // Se o código for inválido ou o email já existir
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint: /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO dto) {
        // Usa o AuthenticationManager configurado no SecurityConfig
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );

            // Se autenticado com sucesso, retorna um OK.
            // Em projetos mais avançados, aqui seria gerado um JWT (Token)
            if (auth.isAuthenticated()) {
                return ResponseEntity.ok("Login bem-sucedido! Bem-vindo, " + auth.getName());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");

        } catch (Exception e) {
            // Captura erros de credenciais
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }
}