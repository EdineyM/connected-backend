-- Tabelas de Referência
CREATE TABLE ref.usuario_tipos (
                                   id SMALLINT PRIMARY KEY,
                                   tipo VARCHAR(50) NOT NULL UNIQUE,
                                   descricao TEXT,
                                   ativo BOOLEAN DEFAULT TRUE,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ref.evento_tipos (
                                  id SMALLINT PRIMARY KEY,
                                  tipo VARCHAR(50) NOT NULL UNIQUE,
                                  descricao TEXT,
                                  ativo BOOLEAN DEFAULT TRUE,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ref.lead_status (
                                 id SMALLINT PRIMARY KEY,
                                 status VARCHAR(50) NOT NULL UNIQUE,
                                 descricao TEXT,
                                 ativo BOOLEAN DEFAULT TRUE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inserir dados iniciais
INSERT INTO ref.usuario_tipos (id, tipo, descricao) VALUES
                                                        (1, 'ADMIN', 'Administrador do sistema'),
                                                        (2, 'REPRESENTANTE', 'Representante da empresa'),
                                                        (3, 'ATENDENTE', 'Atendente de estande'),
                                                        (4, 'PALESTRANTE', 'Palestrante de eventos');

INSERT INTO ref.lead_status (id, status, descricao) VALUES
                                                        (1, 'NOVO', 'Lead recém captado'),
                                                        (2, 'EM_ANDAMENTO', 'Lead em processo de qualificação'),
                                                        (3, 'QUALIFICADO', 'Lead qualificado e pronto para negociação'),
                                                        (4, 'CONVERTIDO', 'Lead convertido em cliente'),
                                                        (5, 'PERDIDO', 'Lead perdido ou desqualificado');