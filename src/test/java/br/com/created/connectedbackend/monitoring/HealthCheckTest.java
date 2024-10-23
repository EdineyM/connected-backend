package br.com.created.connectedbackend.monitoring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthCheckTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve verificar status da aplicação")
    void checkApplicationHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.db.status").value("UP"))
                .andExpect(jsonPath("$.components.diskSpace.status").value("UP"));
    }

    @Test
    @DisplayName("Deve verificar métricas da aplicação")
    void checkApplicationMetrics() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray())
                .andExpect(jsonPath("$.names[?(@=='jvm.memory.used')]").exists());
    }

    @Test
    @DisplayName("Deve verificar informações da aplicação")
    void checkApplicationInfo() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").exists())
                .andExpect(jsonPath("$.app.version").exists())
                .andExpect(jsonPath("$.java.version").exists());
    }

    @Test
    @DisplayName("Deve verificar estado do banco de dados")
    void checkDatabaseHealth() throws Exception {
        mockMvc.perform(get("/actuator/health/db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.database").exists())
                .andExpect(jsonPath("$.details.validationQuery").exists());
    }

    @Test
    @DisplayName("Deve verificar espaço em disco")
    void checkDiskSpace() throws Exception {
        mockMvc.perform(get("/actuator/health/diskSpace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.total").exists())
                .andExpect(jsonPath("$.details.free").exists())
                .andExpect(jsonPath("$.details.threshold").exists());
    }
}