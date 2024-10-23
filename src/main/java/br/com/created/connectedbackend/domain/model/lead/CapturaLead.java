package br.com.created.connectedbackend.domain.model.lead;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "captura_lead", schema = "lead")
public class CapturaLead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false,
            foreignKey = @ForeignKey(name = "captura_lead_lead_id_fkey"))
    private Lead lead;

    @Column(name = "tipo_interacao", nullable = false, length = 50)
    private String tipoInteracao;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}