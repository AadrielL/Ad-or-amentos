package com.adcomandos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AdComandosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdComandosApplication.class, args);
    }

    /**
     * Configuração Global de CORS (Cross-Origin Resource Sharing).
     * Isso é essencial para que aplicações frontend em portas diferentes
     * (como React/Angular/Vue) possam acessar a API REST.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a todas as rotas
                        .allowedOrigins("*") // ⚠️ Permite acesso de qualquer origem (Ajuste para produção!)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite métodos HTTP padrão
                        .allowedHeaders("*"); // Permite todos os cabeçalhos (necessário para o "Authorization")
            }
        };
    }
}