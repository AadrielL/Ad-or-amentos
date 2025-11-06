package com.adcomandos.repository;

import com.adcomandos.model.CodigoAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoAdminRepository extends JpaRepository<CodigoAdmin, Long> {
    Optional<CodigoAdmin> findByCodigoAndStatus(String codigo, String status);
}