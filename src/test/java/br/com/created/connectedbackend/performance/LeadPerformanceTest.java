package br.com.created.connectedbackend.performance;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import br.com.created.connectedbackend.domain.service.lead.LeadService;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LeadPerformanceTest {

    @Autowired
    private LeadService leadService;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;
    private Evento evento;
    private IngressoParticipante ingressoParticipante;

    @BeforeEach
    void setUp() {
        user = TestDataBuilder.createTestUser();
        evento = TestDataBuilder.createTestEvento();
        ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);

        // Limpar dados de teste anteriores
        jdbcTemplate.execute("TRUNCATE TABLE lead.lead CASCADE");
    }

    @Test
    @DisplayName("Deve criar múltiplos leads em batch com performance adequada")
    void batchLeadCreationPerformance() {
        int batchSize = 1000;
        List<CreateLeadRequest> requests = new ArrayList<>();

        // Preparar requests
        IntStream.range(0, batchSize).forEach(i -> {
            requests.add(CreateLeadRequest.builder()
                    .ingressoParticipanteId(ingressoParticipante.getId())
                    .statusId((short) 1)
                    .descricao("Lead de teste " + i)
                    .tipoInteracaoInicial("ABORDAGEM")
                    .build());
        });

        // Medir tempo de execução
        Instant start = Instant.now();

        // Executar em batch
        requests.forEach(request -> leadService.createLead(request, user));

        Duration duration = Duration.between(start, Instant.now());

        // Validar performance
        assertTrue(duration.toMillis() < 5000, "Criação em batch deve levar menos de 5 segundos");
        assertEquals(batchSize, leadRepository.count());
    }

    @Test
    @DisplayName("Deve buscar leads com paginação de forma eficiente")
    void paginatedLeadSearchPerformance() {
        // Criar dados de teste
        createTestLeads(1000);

        Instant start = Instant.now();

        // Realizar múltiplas buscas paginadas
        IntStream.range(0, 10).forEach(i -> {
            var page = leadRepository.findAllByUserIdAndNotDeleted(
                    user.getId(),
                    PageRequest.of(i, 100)
            );
            assertEquals(100, page.getContent().size());
        });

        Duration duration = Duration.between(start, Instant.now());

        // Validar performance de paginação
        assertTrue(duration.toMillis() < 1000, "Buscas paginadas devem levar menos de 1 segundo");
    }

    @Test
    @DisplayName("Deve suportar acessos concorrentes")
    void concurrentAccessPerformance() throws Exception {
        int numThreads = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Criar operações concorrentes
        for (int i = 0; i < numThreads; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    CreateLeadRequest request = CreateLeadRequest.builder()
                            .ingressoParticipanteId(ingressoParticipante.getId())
                            .statusId((short) 1)
                            .descricao("Lead concorrente")
                            .tipoInteracaoInicial("ABORDAGEM")
                            .build();

                    Lead lead = leadService.createLead(request, user);
                    assertNotNull(lead.getId());
                }
            }, executor));
        }

        // Aguardar conclusão
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Validar resultado
        assertEquals(numThreads * operationsPerThread, leadRepository.count());
    }

    @Test
    @DisplayName("Deve ter performance adequada em consultas complexas")
    void complexQueryPerformance() {
        // Criar dados de teste com relacionamentos
        createTestLeadsWithRelationships(500);

        Instant start = Instant.now();

        // Executar consulta complexa
        var result = jdbcTemplate.queryForList("""
            SELECT l.id, l.status_id, 
                   COUNT(cl.id) as capturas_count,
                   COUNT(ad.id) as audios_count
            FROM lead.lead l
            LEFT JOIN lead.captura_lead cl ON l.id = cl.lead_id
            LEFT JOIN lead.audio_descricao ad ON l.id = ad.lead_id
            WHERE l.deleted_at IS NULL
            GROUP BY l.id, l.status_id
            HAVING COUNT(cl.id) > 0
            ORDER BY capturas_count DESC
            LIMIT 100
            """);

        Duration duration = Duration.between(start, Instant.now());

        // Validar performance
        assertTrue(duration.toMillis() < 1000, "Consulta complexa deve levar menos de 1 segundo");
        assertFalse(result.isEmpty());
    }

    private void createTestLeads(int count) {
        List<Lead> leads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            leads.add(TestDataBuilder.createTestLead(user, ingressoParticipante));
        }
        leadRepository.saveAll(leads);
    }

    private void createTestLeadsWithRelationships(int count) {
        // Implementar criação de leads com capturas e áudios relacionados
        // para testar queries complexas
    }
}