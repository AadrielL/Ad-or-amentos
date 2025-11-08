package com.adcomandos.repository;

import com.adcomandos.model.Material;
import com.adcomandos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    // Método customizado que permite ao OrcamentoService buscar apenas
    // os materiais que pertencem ao usuário logado, garantindo o isolamento de dados.
    List<Material> findByUsuario(Usuario usuario);
}