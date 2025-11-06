package com.adcomandos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (é padrão em APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Define a política de sessão como STATELESS (sem sessão de usuário)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define as regras de autorização
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints públicos (login e criação de admin)
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()

                        // Endpoints que requerem autenticação e a role ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Orçamentos e dados de autônomo requerem ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/orcamentos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/materiais/**").hasRole("ADMIN")

                        // Qualquer outra requisição deve ser autenticada
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}) // Habilita autenticação básica temporariamente
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Adiciona CORS

        return http.build();
    }

    // Bean para o codificador de senhas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para gerenciar a autenticação (necessário para o AuthController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configuração básica de CORS (para permitir o Frontend se conectar)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Altere a origem (AllowedOrigins) para o endereço do seu Frontend em produção!
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}