package com.adcomandos.repository;

import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicoPadraoRepository extends JpaRepository<ServicoPadrao, Long> {

    // Método para isolar os dados: busca apenas os serviços cadastrados por um usuário específico.
    List<ServicoPadrao> findByUsuario(Usuario usuario);
}