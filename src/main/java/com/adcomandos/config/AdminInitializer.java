package com.adcomandos.config;

import com.adcomandos.model.CodigoAdmin;
import com.adcomandos.repository.CodigoAdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class AdminInitializer {

    // ⚠️ ATENÇÃO: Código de Ativação Único.
    // Este código deve ser usado no primeiro registro de ADMIN.
    private static final String DEFAULT_ADMIN_CODE = "ADCOMANDOS123";

    /**
     * O CommandLineRunner é executado assim que o Spring Boot é inicializado.
     */
    @Bean
    public CommandLineRunner initAdminCode(CodigoAdminRepository codigoAdminRepository) {
        return args -> {
            // Verifica se o código de ativação já existe no banco de dados.
            // Isso previne que o código seja inserido novamente após o primeiro uso.
            Optional<CodigoAdmin> existingCode = codigoAdminRepository.findByCodigo(DEFAULT_ADMIN_CODE);

            if (existingCode.isEmpty()) {
                CodigoAdmin adminCode = new CodigoAdmin();
                adminCode.setCodigo(DEFAULT_ADMIN_CODE);
                adminCode.setStatus(CodigoAdmin.Status.ATIVO);

                codigoAdminRepository.save(adminCode);
                System.out.println("-----------------------------------------------------------------");
                System.out.println("🔑 CÓDIGO DE ATIVAÇÃO ADMIN INSERIDO: " + DEFAULT_ADMIN_CODE);
                System.out.println("-----------------------------------------------------------------");
            }
        };
    }
}