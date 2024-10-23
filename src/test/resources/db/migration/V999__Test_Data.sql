-- Inserir dados de teste nas tabelas de referência
INSERT INTO ref.usuario_tipos (id, tipo, descricao) VALUES
                                                        (1, 'ADMIN', 'Administrador do sistema'),
                                                        (2, 'REPRESENTANTE', 'Representante da empresa'),
                                                        (3, 'ATENDENTE', 'Atendente de estande'),
                                                        (4, 'PALESTRANTE', 'Palestrante de eventos');

INSERT INTO ref.lead_status (id, status, descricao) VALUES
                                                        (1, 'NOVO', 'Lead recém captado'),
                                                        (2, 'EM_ANDAMENTO', 'Lead em processo de qualificação'),
                                                        (3, 'QUALIFICADO', 'Lead qualificado'),
                                                        (4, 'CONVERTIDO', 'Lead convertido em cliente'),
                                                        (5, 'PERDIDO', 'Lead perdido ou desqualificado');

INSERT INTO ref.evento_tipos (id, tipo, descricao) VALUES
                                                       (1, 'CONFERENCIA', 'Conferência'),
                                                       (2, 'FEIRA', 'Feira de negócios'),
                                                       (3, 'WORKSHOP', 'Workshop prático');

INSERT INTO ref.evento_modalidades (id, modalidade, descricao) VALUES
                                                                   (1, 'PRESENCIAL', 'Evento presencial'),
                                                                   (2, 'ONLINE', 'Evento online'),
                                                                   (3, 'HIBRIDO', 'Evento híbrido');

INSERT INTO ref.evento_status (id, status, descricao) VALUES
                                                          (1, 'PLANEJAMENTO', 'Em planejamento'),
                                                          (2, 'CONFIRMADO', 'Confirmado'),
                                                          (3, 'EM_ANDAMENTO', 'Em andamento'),
                                                          (4, 'FINALIZADO', 'Finalizado'),
                                                          (5, 'CANCELADO', 'Cancelado');