package com.adcomandos.config;

import com.adcomandos.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64; // ✅ USO DE BASE64 MODERNO DO JAVA
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // --- MÉTODOS PÚBLICOS DE EXPOSIÇÃO ---

    public String generateToken(Usuario userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getRole());
        return doGenerateToken(claims, userDetails.getEmail());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // --- MÉTODOS AUXILIARES ---

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtém todas as claims do token. Usa Jwts.parser() (0.9.1).
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            // Sintaxe Jwts.parser() (API antiga)
            return Jwts.parser()
                    // setSigningKey aceita a Key gerada por getSecretKey()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Token JWT inválido ou expirado.", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getClaimFromToken(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    /**
     * Constrói o token JWT.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // ✅ CORREÇÃO: Adicionando o SignatureAlgorithm (HS256)
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, getSecretKey())
                .compact();
    }

    /**
     * Gera a chave de assinatura (Key) a partir da string secreta usando Base64 moderno.
     */
    private Key getSecretKey() {
        // ✅ DECODIFICAÇÃO COM java.util.Base64 (COMPATÍVEL COM JAVA 21)
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(secret);
        // Cria uma Key com o algoritmo HmacSHA256
        return new SecretKeySpec(apiKeySecretBytes, "HmacSHA256");
    }
}