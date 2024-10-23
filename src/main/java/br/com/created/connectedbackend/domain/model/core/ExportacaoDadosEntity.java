package br.com.created.connectedbackend.domain.model.core;

import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.ref.ExportacaoStatus;
import br.com.created.connectedbackend.domain.model.ref.ExportacaoTipo;
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
@Table(name = "exportacao_dados", schema = "core")
public class ExportacaoDados {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_dados", nullable = false, foreignKey = @ForeignKey(name = "exportacao_dados_tipo_dados_fkey"))
    private ExportacaoTipo tipoDados;

    @Column(name = "filtros")
    private String filtros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", nullable = false, foreignKey = @ForeignKey(name = "exportacao_dados_status_fkey"))
    private ExportacaoStatus status;

    @Column(name = "url_arquivo", length = 512)
    private String urlArquivo;

    @Column(name = "formato", length = 10, nullable = false, columnDefinition = "varchar(10) default 'CSV'")
    private String formato;

    @Column(name = "delimitador", length = 1, nullable = false, columnDefinition = "char(1) default ','")
    private Character delimitador;

    @Column(name = "encoding", length = 20, nullable = false, columnDefinition = "varchar(20) default 'UTF-8'")
    private String encoding;

    @Column(name = "qtd_registros")
    private Integer qtdRegistros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false, foreignKey = @ForeignKey(name = "exportacao_dados_evento_id_fkey"))
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "exportacao_dados_usuario_id_fkey"))
    private Usuario usuario;

    @Column(name = "mensagem_erro")
    private String mensagemErro;

    @Column(name = "data_solicitacao", nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        dataSolicitacao = LocalDateTime.now();
        if (formato == null) {
            formato = "CSV";
        }
        if (delimitador == null) {
            delimitador = ',';
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}