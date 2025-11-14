package com.adcomandos.repository;

import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoPadraoRepository extends JpaRepository<ServicoPadrao, Long> {

    /**
     * Busca uma regra de serviço padrão pelo nome (usado aqui como complexidade)
     * e garante que ela pertença ao Admin.
     */
    Optional<ServicoPadrao> findByNomeAndUsuario(String nome, Usuario usuario);

    /**
     * Retorna todos os serviços criados por um usuário específico.
     * Esta assinatura é necessária para compilar o ServicoPadraoService.
     */
    List<ServicoPadrao> findAllByUsuario(Usuario usuario);
}