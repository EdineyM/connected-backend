package br.com.created.connectedbackend.infrastructure.exception;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.DuplicatedLeadException;
import br.com.created.connectedbackend.domain.exception.LeadNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Controller de teste interno
    @RestController
    static class TestController {
        @GetMapping("/test/lead-not-found")
        public void throwLeadNotFoundException() {
            throw new LeadNotFoundException("Lead não encontrado");
        }

        @GetMapping("/test/duplicated-lead")
        public void throwDuplicatedLeadException() {
            throw new DuplicatedLeadException("Lead duplicado");
        }

        @GetMapping("/test/business-error")
        public void throwBusinessException() {
            throw new BusinessException("Erro de negócio");
        }

        @GetMapping("/test/generic-error")
        public void throwGenericException() {
            throw new RuntimeException("Erro genérico");
        }
    }

    @Test
    @DisplayName("Deve tratar LeadNotFoundException")
    void handleLeadNotFoundException() throws Exception {
        mockMvc.perform(get("/test/lead-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Lead não encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve tratar DuplicatedLeadException")
    void handleDuplicatedLeadException() throws Exception {
        mockMvc.perform(get("/test/duplicated-lead"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Lead duplicado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve tratar BusinessException")
    void handleBusinessException() throws Exception {
        mockMvc.perform(get("/test/business-error"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Erro de negócio"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve tratar exceção genérica")
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}