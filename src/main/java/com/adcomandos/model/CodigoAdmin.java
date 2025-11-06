package com.adcomandos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_admin")
@Data
public class CodigoAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String status; // 'VALIDO', 'USADO', 'EXPIRADO'

    private LocalDateTime dataExpiracao;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}