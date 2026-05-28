/**
 * Project: bigrun-coach
 * File: ChatE2eIT
 * ------------------------------------------------------------------------
 * Description:
 * End-to-end integration tests for POST /api/v1/chat using a real Ollama
 * instance and a real pgvector via Testcontainers. Verifies the full stack:
 * HTTP → Controller → ChatService → RagService → Ollama → response.
 * Requires Ollama running at OLLAMA_BASE_URL (default: http://localhost:11434).
 * Runs in live-ai profile (nightly after deploy).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/05/2026
 */
package pl.bigrun.bigruncoach.e2e;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.bigrun.bigruncoach.config.KnowledgeLoader;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-local")
@Testcontainers
@Tag("live-ai")
class ChatE2eIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @LocalServerPort
    int port;

    @MockitoBean
    KnowledgeLoader knowledgeLoader;

    RequestSpecification requestSpec;

    @BeforeEach
    void setUp() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(JSON)
                .build();
    }

    @Test
    void shouldReturn200WithAnswerForToolCallingQuestion() {
        given(requestSpec)
                .body("{\"message\": \"Jakie tempo na maraton 3h?\"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("answer", not(emptyString()))
                .time(lessThan(30000L), MILLISECONDS);
    }

    @Test
    void shouldReturn200WithAnswerForRagQuestion() {
        given(requestSpec)
                .body("{\"message\": \"Jak oddychać przy silnym wietrze?\"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("answer", not(emptyString()))
                .time(lessThan(30000L), MILLISECONDS);
    }

    @Test
    void shouldReturn200WithAnswerForFastModel() {
        given(requestSpec)
                .body("{\"message\": \"Ile wody pić podczas maratonu?\", \"model\": \"fast\"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("answer", not(emptyString()))
                .time(lessThan(30000L), MILLISECONDS);
    }

    @Test
    void shouldReturn200WithAnswerForSmartModel() {
        given(requestSpec)
                .body("{\"message\": \"Ile wody pić podczas maratonu?\", \"model\": \"smart\"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("answer", not(emptyString()))
                .time(lessThan(30000L), MILLISECONDS);
    }

    @Test
    void shouldReturn200WithAnswerWhenModelNotProvided() {
        given(requestSpec)
                .body("{\"message\": \"Ile wody pić podczas maratonu?\"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("answer", not(emptyString()))
                .time(lessThan(30000L), MILLISECONDS);
    }

    @Test
    void shouldReturn200WithAnswerWhenModelIsWhitespace() {
        given(requestSpec)
                .body("{\"message\": \"Ile wody pić podczas maratonu?\", \"model\": \" \"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("answer", not(emptyString()))
                .time(lessThan(30000L), MILLISECONDS);
    }
}
