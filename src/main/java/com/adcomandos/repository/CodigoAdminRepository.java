package com.adcomandos.repository;

import com.adcomandos.model.CodigoAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoAdminRepository extends JpaRepository<CodigoAdmin, Long> {

    // Método necessário para verificar se o código inicial já existe.
    Optional<CodigoAdmin> findByCodigo(String codigo);

    // Este método é usado no AuthController/AdminService para o registro.
    Optional<CodigoAdmin> findByCodigoAndStatus(String codigo, CodigoAdmin.Status status);
}