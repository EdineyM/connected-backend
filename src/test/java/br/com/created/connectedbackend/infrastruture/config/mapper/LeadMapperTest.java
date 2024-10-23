package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.response.lead.AudioDescricaoResponse;
import br.com.created.connectedbackend.api.dto.response.lead.CapturaLeadResponse;
import br.com.created.connectedbackend.api.dto.response.lead.IngressoParticipanteResponse;
import br.com.created.connectedbackend.api.dto.response.lead.LeadResponse;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LeadMapperTest {

    @Autowired
    private LeadMapper leadMapper;

    private User user;
    private Evento evento;
    private IngressoParticipante ingressoParticipante;
    private Lead lead;
    private CapturaLead capturaLead;
    private AudioDescricao audioDescricao;

    @BeforeEach
    void setUp() {
        // Criar dados de teste
        user = TestDataBuilder.createTestUser();
        evento = TestDataBuilder.createTestEvento();
        ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        capturaLead = TestDataBuilder.createTestCapturaLead(lead);
        audioDescricao = TestDataBuilder.createTestAudioDescricao(lead);
    }

    @Test
    @DisplayName("Deve mapear Lead para LeadResponse")
    void toLeadResponse() {
        LeadResponse response = leadMapper.toLeadResponse(lead);

        assertNotNull(response);
        assertEquals(lead.getId(), response.getId());
        assertEquals(lead.getUser().getId(), response.getUserId());
        assertEquals(lead.getUser().getNome(), response.getUserName());
        assertEquals(lead.getStatusId(), response.getStatusId());
        assertEquals(lead.getDescricao(), response.getDescricao());
        assertEquals(lead.getLocalizacao(), response.getLocalizacao());
        assertNotNull(response.getIngressoParticipante());
    }

    @Test
    @DisplayName("Deve mapear IngressoParticipante para IngressoParticipanteResponse")
    void toIngressoParticipanteResponse() {
        IngressoParticipanteResponse response = leadMapper.toIngressoParticipanteResponse(ingressoParticipante);

        assertNotNull(response);
        assertEquals(ingressoParticipante.getId(), response.getId());
        assertEquals(ingressoParticipante.getNome(), response.getNome());
        assertEquals(ingressoParticipante.getSobrenome(), response.getSobrenome());
        assertEquals(ingressoParticipante.getEmail(), response.getEmail());
        assertEquals(ingressoParticipante.getTelefone(), response.getTelefone());
        assertEquals(ingressoParticipante.getEvento().getId(), response.getEventoId());
    }

    @Test
    @DisplayName("Deve mapear CapturaLead para CapturaLeadResponse")
    void toCapturaLeadResponse() {
        CapturaLeadResponse response = leadMapper.toCapturaLeadResponse(capturaLead);

        assertNotNull(response);
        assertEquals(capturaLead.getId(), response.getId());
        assertEquals(capturaLead.getLead().getId(), response.getLeadId());
        assertEquals(capturaLead.getTipoInteracao(), response.getTipoInteracao());
        assertEquals(capturaLead.getDescricao(), response.getDescricao());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Deve mapear AudioDescricao para AudioDescricaoResponse")
    void toAudioDescricaoResponse() {
        AudioDescricaoResponse response = leadMapper.toAudioDescricaoResponse(audioDescricao);

        assertNotNull(response);
        assertEquals(audioDescricao.getId(), response.getId());
        assertEquals(audioDescricao.getLead().getId(), response.getLeadId());
        assertEquals(audioDescricao.getUrlS3(), response.getUrlS3());
        assertEquals(audioDescricao.getTranscricao(), response.getTranscricao());
        assertEquals(audioDescricao.getArquivado(), response.getArquivado());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Deve lidar com valores nulos")
    void handleNullValues() {
        lead.setDescricao(null);
        lead.setLocalizacao(null);

        LeadResponse response = leadMapper.toLeadResponse(lead);

        assertNotNull(response);
        assertNull(response.getDescricao());
        assertNull(response.getLocalizacao());
    }
}