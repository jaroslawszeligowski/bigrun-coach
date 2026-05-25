/**
 * Project: bigrun-coach
 * File: RagServiceIT
 * ------------------------------------------------------------------------
 * Description:
 * Integration tests for RagService using a real Ollama (nomic-embed-text)
 * and a real pgvector instance via Testcontainers. Verifies that semantic
 * similarity search returns relevant documents for given queries.
 * Run locally with: mvn test -Dgroups=local
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 25/05/2026
 */
package pl.bigrun.bigruncoach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.bigrun.bigruncoach.config.KnowledgeLoader;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test-local")
@Testcontainers
class RagServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @Autowired
    VectorStore vectorStore;

    @Autowired
    RagService ragService;

    @MockitoBean
    KnowledgeLoader knowledgeLoader;

    @BeforeEach
    void loadDocuments() {
        vectorStore.add(List.of(
                new Document("Podczas biegu oddychaj rytmicznie. Przy wietrze oddychaj przez nos.",
                        Map.of("source", "breathing.md")),
                new Document("Nawodnienie przed maratonem jest kluczowe. Pij wodę co 20 minut.",
                        Map.of("source", "hydration.md")),
                new Document("Tempo na maraton 3h to 4:16 min/km. Stosuj negative split.",
                        Map.of("source", "marathon-prep.md")),
                new Document("Kolano biegacza to najczęstsza kontuzja. Wzmocnij mięśnie bioder i unikaj zwiększania kilometrażu o więcej niż 10% tygodniowo.",
                        Map.of("source", "injuries.md")),
                new Document("Temperatura ma kluczowy wpływ na bieganie. Optymalna temperatura wyścigu to 8-12°C. Powyżej 24°C zmniejsz tempo o 10-15%.",
                        Map.of("source", "weather-impact.md"))
        ));
    }

    @Test
    void shouldFindBreathingContext() {
        String context = ragService.findRelevantContext("jak oddychać podczas biegu przy wietrze");
        assertThat(context)
                .satisfiesAnyOf(
                        c->assertThat(c).containsIgnoringCase("oddychać"),
                        c->assertThat(c).containsIgnoringCase("oddychaj"));
    }

    @Test
    void shouldFindHydrationContext() {
        String context = ragService.findRelevantContext("ile wody pić podczas maratonu");
        assertThat(context)
                .satisfiesAnyOf(
                        c->assertThat(c).containsIgnoringCase("pić"),
                        c->assertThat(c).containsIgnoringCase("pij"));
    }

    @Test
    void shouldFindMarathonPaceContext() {
        String context = ragService.findRelevantContext("jakie tempo na maraton");
        assertThat(context)
                .satisfiesAnyOf(
                        c->assertThat(c).containsIgnoringCase("tempo"),
                        c->assertThat(c).containsIgnoringCase("czas"));
    }

    @Test
    void shouldFindInjuriesContext() {
        String context = ragService.findRelevantContext("jak leczyć kolano biegacza");
        assertThat(context)
                .satisfiesAnyOf(
                        c -> assertThat(c).containsIgnoringCase("kolano"),
                        c -> assertThat(c).containsIgnoringCase("kontuzja"),
                        c -> assertThat(c).containsIgnoringCase("kilometraż"));
    }

    @Test
    void shouldFindWeatherImpactContext() {
        String context = ragService.findRelevantContext("jak temperatura wpływa na bieganie");
        assertThat(context)
                .satisfiesAnyOf(
                        c -> assertThat(c).containsIgnoringCase("temperatura"),
                        c -> assertThat(c).containsIgnoringCase("tempo"),
                        c -> assertThat(c).containsIgnoringCase("ciepło"));
    }

    @Test
    void shouldReturnSomethingForUnrelatedQuery() {
        String context = ragService.findRelevantContext("przepis na pizzę");
        assertThat(context).isNotNull();
    }
}
