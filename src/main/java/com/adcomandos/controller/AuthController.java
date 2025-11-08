package com.adcomandos.controller;

import com.adcomandos.dto.LoginRequestDTO;
import com.adcomandos.dto.RegistroAdminRequestDTO;
import com.adcomandos.dto.TokenResponseDTO;
import com.adcomandos.model.Usuario;
import com.adcomandos.service.AdminService;
import com.adcomandos.config.JwtTokenUtil; // ⬅️ Assumindo que você tem uma classe JwtTokenUtil
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(AdminService adminService,
                          AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * POST /auth/registrar: Permite o registro do primeiro ADMIN usando o código de ativação.
     */
    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrarAdmin(@RequestBody @Valid RegistroAdminRequestDTO request) {
        try {
            Usuario novoAdmin = adminService.registrarAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request em caso de e-mail duplicado ou código inválido
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /auth/login: Autentica o usuário e gera um token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        try {
            // 1. Autentica o usuário usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            // 2. Gera o Token JWT
            // Note: O principal retornado é o nosso objeto Usuario
            final String token = jwtTokenUtil.generateToken((Usuario) authentication.getPrincipal());

            // 3. Retorna o Token
            return ResponseEntity.ok(new TokenResponseDTO(token));

        } catch (Exception e) {
            // Geralmente 401 Unauthorized para credenciais inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}