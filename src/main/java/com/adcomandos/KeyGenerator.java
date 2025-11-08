package com.adcomandos; // Ou use o pacote raiz do seu projeto

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class KeyGenerator {
    public static void main(String[] args) {
        // Gera uma chave segura de 256 bits (32 bytes)
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Codifica para Base64 para ser usada no application.properties
        String base64Key = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("------------------------------------------------------------------");
        System.out.println("🔑 CHAVE SECRETA JWT GERADA (COPIE E COLE NO application.properties):");
        System.out.println(base64Key);
        System.out.println("------------------------------------------------------------------");
    }
}