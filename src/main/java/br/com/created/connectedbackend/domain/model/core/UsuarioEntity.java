package br.com.created.connectedbackend.domain.model.core;

import br.com.created.connectedbackend.domain.model.address.Endereco;
import br.com.created.connectedbackend.domain.model.ref.UsuarioTipo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "usuario", schema = "core")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Email
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "senha_salt", nullable = false)
    private String senhaSalt;

    @Pattern(regexp = "^\\+?[1-9]\\d{10,14}$", message = "O telefone deve conter entre 10 e 15 dígitos, incluindo o código do país (opcional)")
    @Column(name = "telefone", length = 15)
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false, foreignKey = @ForeignKey(name = "usuario_tipo_id_fkey"))
    private UsuarioTipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "usuario_empresa_id_fkey"))
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false, foreignKey = @ForeignKey(name = "usuario_endereco_id_fkey"))
    private Endereco endereco;

    @Column(name = "foto_url", length = 512)
    private String fotoUrl;

    @Column(name = "bio")
    private String bio;

    @Column(name = "ativo", nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (ativo == null) {
            ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}