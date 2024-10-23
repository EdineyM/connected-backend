package br.com.created.connectedbackend.domain.model.event;

import br.com.created.connectedbackend.domain.model.core.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario_estande", schema = "event")
public class UsuarioEstandeEntity {

    @EmbeddedId
    private UsuarioEstandeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "usuario_estande_usuario_id_fkey"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("estandeId")
    @JoinColumn(name = "estande_id", nullable = false, foreignKey = @ForeignKey(name = "usuario_estande_estande_id_fkey"))
    private Estande estande;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}