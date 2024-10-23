-- Tabela de Ingresso Participante
CREATE TABLE lead.ingresso_participante (
                                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                            nome VARCHAR(255) NOT NULL,
                                            sobrenome VARCHAR(255) NOT NULL,
                                            email VARCHAR(255) NOT NULL,
                                            telefone VARCHAR(15),
                                            cpf VARCHAR(11),
                                            qr_code VARCHAR(255) UNIQUE,
                                            nome_empresa VARCHAR(255),
                                            cnpj_empresa VARCHAR(14),
                                            cargo VARCHAR(100),
                                            setor VARCHAR(100),
                                            porte_empresa VARCHAR(50),
                                            categoria VARCHAR(100),
                                            evento_id UUID NOT NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP,
                                            deleted_at TIMESTAMP,
                                            CONSTRAINT ck_cpf_format CHECK (cpf ~ '^\d{11}$'),
                                            CONSTRAINT ck_telefone_format CHECK (telefone ~ '^\+?[1-9]\d{10,14}$'),
                                            CONSTRAINT ck_cnpj_format CHECK (cnpj_empresa ~ '^\d{14}$'),
                                            CONSTRAINT uk_cpf_evento UNIQUE (cpf, evento_id)
);

-- Tabela de Lead
CREATE TABLE lead.lead (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           usuario_id UUID NOT NULL,
                           ingresso_id UUID NOT NULL,
                           status_id SMALLINT NOT NULL,
                           descricao TEXT,
                           localizacao VARCHAR(255),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           deleted_at TIMESTAMP,
                           CONSTRAINT fk_lead_usuario FOREIGN KEY (usuario_id) REFERENCES core.usuario(id),
                           CONSTRAINT fk_lead_ingresso FOREIGN KEY (ingresso_id) REFERENCES lead.ingresso_participante(id),
                           CONSTRAINT fk_lead_status FOREIGN KEY (status_id) REFERENCES ref.lead_status(id)
);

-- Tabela de Captura Lead
CREATE TABLE lead.captura_lead (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   lead_id UUID NOT NULL,
                                   tipo_interacao VARCHAR(50) NOT NULL,
                                   descricao TEXT,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_captura_lead FOREIGN KEY (lead_id) REFERENCES lead.lead(id)
);

-- Tabela de Audio Descrição
CREATE TABLE lead.audio_descricao (
                                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      lead_id UUID NOT NULL,
                                      url_s3 VARCHAR(512) NOT NULL,
                                      transcricao TEXT,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      arquivado BOOLEAN DEFAULT FALSE,
                                      CONSTRAINT fk_audio_lead FOREIGN KEY (lead_id) REFERENCES lead.lead(id)
);

-- Índices
CREATE INDEX idx_ingresso_participante_evento ON lead.ingresso_participante(evento_id);
CREATE INDEX idx_ingresso_participante_cpf ON lead.ingresso_participante(cpf) WHERE deleted_at IS NULL;
CREATE INDEX idx_lead_usuario ON lead.lead(usuario_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_lead_ingresso ON lead.lead(ingresso_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_lead_status ON lead.lead(status_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_captura_lead ON lead.captura_lead(lead_id);
CREATE INDEX idx_audio_descricao_lead ON lead.audio_descricao(lead_id);
CREATE INDEX idx_audio_descricao_arquivado ON lead.audio_descricao(arquivado);