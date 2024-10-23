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
@Table(name = "palestrante_palestra", schema = "event")
public class PalestrantePalestraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "palestrante_id", nullable = false, foreignKey = @ForeignKey(name = "palestrante_palestra_palestrante_id_fkey"))
    private Usuario palestrante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "palestra_id", nullable = false, foreignKey = @ForeignKey(name = "palestrante_palestra_palestra_id_fkey"))
    private Palestra palestra;

    @Column(name = "is_principal", nullable = false, columnDefinition = "boolean default false")
    private Boolean isPrincipal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPrincipal == null) {
            isPrincipal = false;
        }
    }
}