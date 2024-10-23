package br.com.created.connectedbackend.domain.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEstandeId implements Serializable {

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "estande_id")
    private UUID estandeId;
}