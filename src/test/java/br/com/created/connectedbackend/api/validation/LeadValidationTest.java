package br.com.created.connectedbackend.api.validation;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.CreateAudioDescricaoRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class LeadValidationTest {

    private static Validator validator;
    private CreateLeadRequest createLeadRequest;
    private CreateCapturaLeadRequest createCapturaRequest;
    private CreateAudioDescricaoRequest createAudioRequest;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        createLeadRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(UUID.randomUUID())
                .statusId((short) 1)
                .descricao("Descrição do lead")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();

        createCapturaRequest = CreateCapturaLeadRequest.builder()
                .leadId(UUID.randomUUID())
                .tipoInteracao("REUNIAO")
                .descricao("Descrição da captura")
                .build();

        createAudioRequest = CreateAudioDescricaoRequest.builder()
                .leadId(UUID.randomUUID())
                .urlS3("s3://bucket/audio.mp3")
                .transcricao("Transcrição do áudio")
                .build();
    }

    @Test
    @DisplayName("Deve validar CreateLeadRequest válido")
    void validateValidCreateLeadRequest() {
        var violations = validator.validate(createLeadRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve validar CreateLeadRequest com campos obrigatórios ausentes")
    void validateInvalidCreateLeadRequest() {
        createLeadRequest.setIngressoParticipanteId(null);
        createLeadRequest.setTipoInteracaoInicial(null);

        var violations = validator.validate(createLeadRequest);
        assertEquals(2, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("ingressoParticipanteId")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("tipoInteracaoInicial")));
    }

    @Test
    @DisplayName("Deve validar tamanho máximo dos campos do CreateLeadRequest")
    void validateCreateLeadRequestMaxLength() {
        String longText = "a".repeat(1001);
        createLeadRequest.setDescricao(longText);

        var violations = validator.validate(createLeadRequest);
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("descricao")));
    }

    @Test
    @DisplayName("Deve validar CreateCapturaLeadRequest válido")
    void validateValidCreateCapturaRequest() {
        var violations = validator.validate(createCapturaRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve validar CreateCapturaLeadRequest com campos inválidos")
    void validateInvalidCreateCapturaRequest() {
        createCapturaRequest.setLeadId(null);
        createCapturaRequest.setTipoInteracao("");

        var violations = validator.validate(createCapturaRequest);
        assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("Deve validar CreateAudioDescricaoRequest válido")
    void validateValidCreateAudioRequest() {
        var violations = validator.validate(createAudioRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve validar CreateAudioDescricaoRequest com campos inválidos")
    void validateInvalidCreateAudioRequest() {
        createAudioRequest.setLeadId(null);
        createAudioRequest.setUrlS3(null);

        var violations = validator.validate(createAudioRequest);
        assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("Deve validar tamanho da URL S3")
    void validateS3UrlLength() {
        createAudioRequest.setUrlS3("s3://" + "a".repeat(510));

        var violations = validator.validate(createAudioRequest);
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("urlS3")));
    }
}