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
@Table(name = "audio_descricao", schema = "lead")
public class AudioDescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false,
            foreignKey = @ForeignKey(name = "audio_descricao_lead_id_fkey"))
    private Lead lead;

    @Column(name = "url_s3", nullable = false, length = 512)
    private String urlS3;

    @Column(name = "transcricao")
    private String transcricao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "arquivado")
    private Boolean arquivado;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (arquivado == null) {
            arquivado = false;
        }
    }
}