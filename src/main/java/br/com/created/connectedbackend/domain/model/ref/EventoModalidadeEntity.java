package br.com.created.connectedbackend.domain.model.ref;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evento_modalidades", schema = "ref")
public class EventoModalidadeEntity {

    @Id
    @Column(name = "id")
    private Short id;

    @Column(name = "modalidade", nullable = false, unique = true, length = 50)
    private String modalidade;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "ativo", nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}