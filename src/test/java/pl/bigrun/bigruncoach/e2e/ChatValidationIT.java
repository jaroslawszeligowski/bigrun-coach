/**
 * Project: bigrun-coach
 * File: ChatValidationIT
 * ------------------------------------------------------------------------
 * Description:
 * Integration tests verifying input validation and error handling for
 * POST /api/v1/chat. Tests cover blank message, missing field, null value,
 * wrong Content-Type and wrong HTTP method.
 * No Ollama required — requests are rejected before reaching the service layer.
 * Runs in integration-tests profile (daily + nightly before deploy).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/05/2026
 */
package pl.bigrun.bigruncoach.e2e;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.bigrun.bigruncoach.config.KnowledgeLoader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ChatValidationIT {

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
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    void shouldReturn400ForBlankMessage() {
        given(requestSpec)
                .body("{\"message\": \"\"}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", not(emptyString()));
    }

    @Test
    void shouldReturn400ForMissingMessageField() {
        given(requestSpec)
                .body("{}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", not(emptyString()));
    }

    @Test
    void shouldReturn400ForNullMessage() {
        given(requestSpec)
                .body("{\"message\": null}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", not(emptyString()));
    }

    @Test
    void shouldReturn415ForWrongContentType() {
        given(requestSpec)
                .contentType("text/plain")
                .body("jakie tempo?")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(415);
    }

    @Test
    void shouldReturn405ForGetMethod() {
        given(requestSpec)
                .body("{\"message\": \"test\"}")
        .when()
                .get("/api/v1/chat")
        .then()
                .statusCode(405);
    }

    @Test
    void shouldReturn400ForMalformedJsonModelWithoutQuotes() {
        given(requestSpec)
                .body("{\"message\": \"test\", \"model\": xyz}")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", not(emptyString()));
    }

    @Test
    void shouldReturn400ForMalformedJsonModelMissingValue() {
        given(requestSpec)
                .body("{\"message\": \"test\", \"model\": }")
        .when()
                .post("/api/v1/chat")
        .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", not(emptyString()));
    }
}
