/**
 * Project: bigrun-coach
 * File: ChatServiceIT
 * ------------------------------------------------------------------------
 * Description:
 * Integration tests for ChatServiceImpl. Uses WireMock to stub Ollama
 * endpoints (/api/embed, /api/chat) and Testcontainers for a real pgvector
 * instance. KnowledgeLoader is mocked to prevent embedding calls on startup.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 25/05/2026
 */
package pl.bigrun.bigruncoach.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@EnableWireMock
class ChatServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @InjectWireMock
    WireMockServer wireMock;

    @Autowired
    ChatService chatService;

    @MockitoBean
    KnowledgeLoader knowledgeLoader;

    @BeforeEach
    void stubOllama() throws IOException {
        String embedBody = new ClassPathResource("wiremock/ollama-embed-response.json")
                .getContentAsString(StandardCharsets.UTF_8);
        String chatBody = new ClassPathResource("wiremock/ollama-chat-response.json")
                .getContentAsString(StandardCharsets.UTF_8);

        wireMock.stubFor(post(urlPathEqualTo("/api/embed"))
                .willReturn(okJson(embedBody)));

        wireMock.stubFor(post(urlPathEqualTo("/api/chat"))
                .willReturn(okJson(chatBody)));
    }

    @Test
    void shouldReturnAnswerForValidMessage() {
        String answer = chatService.chat("What pace for a 3h marathon?", null);
        assertThat(answer).isNotBlank();
    }

    @Test
    void shouldReturnContentFromLlm() {
        String answer = chatService.chat("What pace for a 3h marathon?", "fast");
        assertThat(answer).contains("4:16");
    }

    @Test
    void shouldHandleFastModelRequest() {
        String answer = chatService.chat("Hydration tips?", "fast");
        assertThat(answer).isNotBlank();
    }
}
