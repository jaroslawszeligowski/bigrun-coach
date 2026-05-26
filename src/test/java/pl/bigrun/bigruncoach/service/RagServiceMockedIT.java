/**
 * Project: bigrun-coach
 * File: RagServiceMockedIT
 * ------------------------------------------------------------------------
 * Description:
 * Integration tests for RagService with WireMock stubbing Ollama /api/embed.
 * Verifies the RAG plumbing (VectorStore add + similarity query) without
 * requiring a real Ollama instance. Runs in both daily and nightly pipelines
 * before deploy. Semantic correctness is NOT verified here — all documents
 * receive identical fake embeddings, so only null/blank checks are applied.
 * For semantic assertions see RagServiceIT (tag: rag, nightly after deploy).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 26/05/2026
 */
package pl.bigrun.bigruncoach.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import pl.bigrun.bigruncoach.config.KnowledgeLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Testcontainers
@EnableWireMock
class RagServiceMockedIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @InjectWireMock
    WireMockServer wireMock;

    @Autowired
    VectorStore vectorStore;

    @Autowired
    RagService ragService;

    @MockitoBean
    KnowledgeLoader knowledgeLoader;

    @BeforeEach
    void stubAndLoad() throws IOException {
        String embedBody = new ClassPathResource("wiremock/ollama-embed-response.json")
                .getContentAsString(StandardCharsets.UTF_8);

        wireMock.stubFor(post(urlPathEqualTo("/api/embed"))
                .willReturn(okJson(embedBody)));

        // Dokumenty dodawane po jednym — każde wywołanie wysyła 1 tekst do /api/embed.
        // Fixture zwraca 1 embedding → bez błędu ArrayIndexOutOfBoundsException.
        // Batch (List.of 5 dokumentów) wysyłałby 5 tekstów naraz → fixture zwraca 1 → błąd na get(1).
        for (Document doc : List.of(
                new Document("Podczas biegu oddychaj rytmicznie. Przy wietrze oddychaj przez nos.",
                        Map.of("source", "breathing.md")),
                new Document("Nawodnienie przed maratonem jest kluczowe. Pij wodę co 20 minut.",
                        Map.of("source", "hydration.md")),
                new Document("Tempo na maraton 3h to 4:16 min/km. Stosuj negative split.",
                        Map.of("source", "marathon-prep.md")),
                new Document("Kolano biegacza to najczęstsza kontuzja.",
                        Map.of("source", "injuries.md")),
                new Document("Temperatura ma kluczowy wpływ na bieganie.",
                        Map.of("source", "weather-impact.md"))
        )) {
            vectorStore.add(List.of(doc));
        }
    }

    // Każdy test sprawdza wyłącznie że:
    //   - WireMock odpowiedział na /api/embed
    //   - pgvector zapisał i odczytał dokumenty
    //   - RagService nie rzucił wyjątku
    //   - wynik nie jest null
    //
    // Wszystkie dokumenty mają identyczny fake wektor (768 x 0.1) — cosine similarity
    // jest taka sama dla każdego dokumentu, więc pgvector może zwrócić dowolny z nich.
    // Semantyczną poprawność weryfikuje RagServiceIT z prawdziwą Ollama (@Tag("rag"), nightly po deployu).

    @Test
    void shouldReturnResultForBreathingQuery() {
        String context = ragService.findRelevantContext("jak oddychać podczas biegu");
        assertThat(context).isNotNull();
    }

    @Test
    void shouldReturnResultForHydrationQuery() {
        String context = ragService.findRelevantContext("ile wody pić podczas maratonu");
        assertThat(context).isNotNull();
    }

    @Test
    void shouldReturnResultForMarathonPaceQuery() {
        String context = ragService.findRelevantContext("jakie tempo na maraton");
        assertThat(context).isNotNull();
    }

    @Test
    void shouldReturnResultForInjuryQuery() {
        String context = ragService.findRelevantContext("jak leczyć kolano biegacza");
        assertThat(context).isNotNull();
    }

    @Test
    void shouldReturnResultForWeatherQuery() {
        String context = ragService.findRelevantContext("jak temperatura wpływa na bieganie");
        assertThat(context).isNotNull();
    }

    @Test
    void shouldReturnResultForUnrelatedQuery() {
        String context = ragService.findRelevantContext("przepis na pizzę");
        assertThat(context).isNotNull();
    }
}
