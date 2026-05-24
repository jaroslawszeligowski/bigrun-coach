package pl.bigrun.bigruncoach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.bigrun.bigruncoach.config.KnowledgeLoader;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class BigrunCoachApplicationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    // KnowledgeLoader implements ApplicationRunner — Spring calls run() automatically on startup.
    // run() calls vectorStore.add() which sends embedding requests to Ollama.
    // In CI there is no Ollama — mock replaces the bean with a no-op so context loads cleanly.
    @MockitoBean
    KnowledgeLoader knowledgeLoader;

    @Test
    void contextLoads() {
    }
}
