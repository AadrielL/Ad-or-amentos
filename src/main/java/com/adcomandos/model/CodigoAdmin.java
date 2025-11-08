package com.adcomandos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_admin")
@Data
@NoArgsConstructor
public class CodigoAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O código de ativação em si (ex: ADCOMANDOS123)
    @Column(nullable = false, unique = true)
    private String codigo;

    // Status do código: se está ATIVO (pode ser usado) ou USADO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Data em que o código foi usado/criado (opcional)
    private LocalDateTime dataUso;

    // Usuário ao qual o código foi associado após o uso
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public enum Status {
        ATIVO,
        USADO,
        EXPIRADO
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = Status.ATIVO;
        }
    }
}