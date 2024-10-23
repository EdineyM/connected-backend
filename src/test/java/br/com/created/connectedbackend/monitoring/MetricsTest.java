package br.com.created.connectedbackend.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuator.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMetrics
@ActiveProfiles("test")
class MetricsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    @DisplayName("Deve registrar métricas de requisição HTTP")
    @WithMockUser(roles = "ADMIN")
    void recordHttpRequestMetrics() throws Exception {
        // Executar algumas requisições
        mockMvc.perform(get("/api/v1/leads")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/leads")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/leads")).andExpect(status().isOk());

        // Verificar métricas HTTP
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Search.in(meterRegistry)
                    .name("http.server.requests")
                    .tag("uri", "/api/v1/leads")
                    .tag("status", "200")
                    .meters()
                    .forEach(meter ->
                            assertTrue(meter.measure().iterator().next().getValue() >= 3.0)
                    );
        });
    }

    @Test
    @DisplayName("Deve registrar métricas de JVM")
    void recordJvmMetrics() {
        // Verificar métricas de memória
        assertNotNull(meterRegistry.get("jvm.memory.used").gauge());
        assertNotNull(meterRegistry.get("jvm.memory.max").gauge());

        // Verificar métricas de threads
        assertNotNull(meterRegistry.get("jvm.threads.live").gauge());
        assertNotNull(meterRegistry.get("jvm.threads.daemon").gauge());

        // Verificar métricas de GC
        assertNotNull(meterRegistry.get("jvm.gc.memory.allocated").counter());
        assertNotNull(meterRegistry.get("jvm.gc.memory.promoted").counter());
    }

    @Test
    @DisplayName("Deve registrar métricas de sistema")
    void recordSystemMetrics() {
        // Verificar métricas de CPU
        assertNotNull(meterRegistry.get("system.cpu.count").gauge());
        assertNotNull(meterRegistry.get("system.cpu.usage").gauge());

        // Verificar métricas de processador
        assertNotNull(meterRegistry.get("process.cpu.usage").gauge());

        // Verificar métricas de uptime
        assertNotNull(meterRegistry.get("process.uptime").timeGauge());
    }

    @Test
    @DisplayName("Deve registrar métricas de banco de dados")
    void recordDatabaseMetrics() {
        // Verificar métricas de conexão
        assertNotNull(meterRegistry.get("hikaricp.connections.active").gauge());
        assertNotNull(meterRegistry.get("hikaricp.connections.idle").gauge());
        assertNotNull(meterRegistry.get("hikaricp.connections.pending").gauge());

        // Verificar métricas de tempo de conexão
        assertNotNull(meterRegistry.get("hikaricp.connections.creation").timer());
        assertNotNull(meterRegistry.get("hikaricp.connections.timeout").counter());
    }

    @Test
    @DisplayName("Deve registrar métricas customizadas")
    void recordCustomMetrics() {
        // Registrar métrica customizada
        meterRegistry.counter("leads.created").increment();
        meterRegistry.counter("leads.updated").increment();
        meterRegistry.counter("leads.deleted").increment();

        // Verificar métricas
        assertEquals(1, meterRegistry.get("leads.created").counter().count());
        assertEquals(1, meterRegistry.get("leads.updated").counter().count());
        assertEquals(1, meterRegistry.get("leads.deleted").counter().count());
    }

    @Test
    @DisplayName("Deve registrar métricas de cache")
    void recordCacheMetrics() {
        assertNotNull(meterRegistry.get("cache.size").gauge());
        assertNotNull(meterRegistry.get("cache.gets").functionCounter());
        assertNotNull(meterRegistry.get("cache.puts").functionCounter());
        assertNotNull(meterRegistry.get("cache.evictions").functionCounter());
    }
}