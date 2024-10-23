package br.com.created.connectedbackend.util;

import br.com.created.connectedbackend.domain.model.lead.*;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.model.event.Evento;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestDataBuilder {

    public static User createTestUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .nome("Test User")
                .email("test@example.com")
                .telefone("+5511999999999")
                .ativo(true)
                .tipoId((short) 1)
                .build();
    }

    public static IngressoParticipante createTestIngressoParticipante(Evento evento) {
        return IngressoParticipante.builder()
                .id(UUID.randomUUID())
                .nome("John")
                .sobrenome("Doe")
                .email("john@example.com")
                .telefone("+5511988888888")
                .cpf("12345678901")
                .nomeEmpresa("Test Company")
                .cnpjEmpresa("12345678901234")
                .cargo("Gerente")
                .setor("TI")
                .porteEmpresa("Médio")
                .categoria("Visitante")
                .evento(evento)
                .build();
    }

    public static Lead createTestLead(User user, IngressoParticipante ingressoParticipante) {
        return Lead.builder()
                .id(UUID.randomUUID())
                .user(user)
                .ingressoParticipante(ingressoParticipante)
                .statusId((short) 1)
                .descricao("Test lead description")
                .localizacao("Test location")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static CapturaLead createTestCapturaLead(Lead lead) {
        return CapturaLead.builder()
                .id(UUID.randomUUID())
                .lead(lead)
                .tipoInteracao("ABORDAGEM")
                .descricao("Test capture description")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static AudioDescricao createTestAudioDescricao(Lead lead) {
        return AudioDescricao.builder()
                .id(UUID.randomUUID())
                .lead(lead)
                .urlS3("s3://test-bucket/test-audio.mp3")
                .transcricao("Test audio transcription")
                .arquivado(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Evento createTestEvento() {
        return Evento.builder()
                .id(UUID.randomUUID())
                .nome("Test Event")
                .descricao("Test event description")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .tipoId((short) 1)
                .modalidadeId((short) 1)
                .statusId((short) 1)
                .build();
    }
}