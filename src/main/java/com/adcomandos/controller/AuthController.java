package com.adcomandos.controller;



import com.adcomandos.config.JwtTokenUtil;

import com.adcomandos.dto.LoginRequestDTO; // ⬅️ DTO de Entrada

import com.adcomandos.dto.TokenResponseDTO; // ⬅️ DTO de Saída

import com.adcomandos.model.Usuario;

import com.adcomandos.service.UsuarioService;

import jakarta.validation.Valid; // Para validar o DTO

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;



@RestController

@RequestMapping("/api/auth")

public class AuthController {



private final AuthenticationManager authenticationManager;

private final JwtTokenUtil jwtTokenUtil;

private final UsuarioService usuarioService;



public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UsuarioService usuarioService) {

this.authenticationManager = authenticationManager;

this.jwtTokenUtil = jwtTokenUtil;

this.usuarioService = usuarioService;

}



@PostMapping("/login")

// ✅ Note a anotação @Valid para aplicar as regras de NotBlank/Email

public ResponseEntity<TokenResponseDTO> createAuthenticationToken(@Valid @RequestBody LoginRequestDTO loginRequestDTO) throws Exception {



// 1. Tenta autenticar o usuário

authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(

// Usa o email do DTO

loginRequestDTO.getEmail(),

// Usa a senha do DTO

loginRequestDTO.getSenha())

);



// 2. Carrega os detalhes do usuário (UserDetails)

final UserDetails userDetails = usuarioService

.loadUserByUsername(loginRequestDTO.getEmail());



// 3. Gera o token

final String token = jwtTokenUtil.generateToken((Usuario) userDetails);



// 4. Retorna a resposta no formato TokenResponseDTO

return ResponseEntity.ok(new TokenResponseDTO(token));

}

}