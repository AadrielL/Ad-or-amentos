package com.adcomandos;

import com.adcomandos.config.JwtRequestFilter;
import com.adcomandos.config.JwtTokenUtil;
import com.adcomandos.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AdComandosApplicationTests {

    // Simula o utilitário JWT. Impede que o @Value do jwt.secret falhe
    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    // Simula o filtro JWT. Impede que ele procure o UsuarioService real
    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    // Simula o serviço de usuário. Impede que ele procure o repositório (DB)
    @MockBean
    private UsuarioService usuarioService;

    @Test
    void contextLoads() {
        // Se este teste passar, significa que o contexto do Spring subiu,
        // ignorando com sucesso as dependências complexas.
    }
}