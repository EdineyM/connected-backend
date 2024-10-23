package br.com.created.connectedbackend.domain.model.lead;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.created.connectedbackend.domain.model.event.Evento;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ingresso_participante", schema = "lead",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cpf_evento", columnNames = {"cpf", "evento_id"}),
                @UniqueConstraint(name = "ingresso_participante_qr_code_key", columnNames = {"qr_code"})
        })
public class IngressoParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "sobrenome", nullable = false, length = 255)
    private String sobrenome;

    @Email
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{10,14}$")
    @Column(name = "telefone", length = 15)
    private String telefone;

    @Pattern(regexp = "^\\d{11}$")
    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "qr_code", length = 255, unique = true)
    private String qrCode;

    @Column(name = "nome_empresa", length = 255)
    private String nomeEmpresa;

    @Pattern(regexp = "^\\d{14}$")
    @Column(name = "cnpj_empresa", length = 14)
    private String cnpjEmpresa;

    @Column(name = "cargo", length = 100)
    private String cargo;

    @Column(name = "setor", length = 100)
    private String setor;

    @Column(name = "porte_empresa", length = 50)
    private String porteEmpresa;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false,
            foreignKey = @ForeignKey(name = "ingresso_participante_evento_id_fkey"))
    private Evento evento;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "ingressoParticipante")
    private List<Lead> leads;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}