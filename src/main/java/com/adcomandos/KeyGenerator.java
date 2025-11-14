package com.adcomandos;// Em KeyGenerator.java

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        // Codifica usando Base64 moderno
        String base64Key = Base64.getEncoder().encodeToString(bytes);

        System.out.println("Sua nova chave secreta JWT (32 bytes Base64):");
        System.out.println(base64Key);
    }
}