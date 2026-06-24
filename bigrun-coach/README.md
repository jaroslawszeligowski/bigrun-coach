# bigrun-coach — Backend

Spring Boot 3.5 / Java 21 backend for BigRun Coach.
See the [root README](../README.md) for architecture overview and Quick Start.

## Local development

**Requirements:** Java 21, Maven 3.9, PostgreSQL 16 with pgvector

```bash
cp .env.example .env
```

Fill in DB credentials and choose one AI provider:

**Option 1 — Ollama (local, ~10 GB RAM)**
```env
OLLAMA_ENABLED=true
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_SMART_MODEL=qwen2.5:14b
OLLAMA_FAST_MODEL=llama3.2:3b
OLLAMA_BIELIK_MODEL=SpeakLeash/bielik-11b-v3.0-instruct:Q4_K_M
OLLAMA_EMBEDDING_MODEL=nomic-embed-text
```

> Model names can be replaced with any Ollama-compatible model — defaults shown above.
> Model must be pulled first: `ollama pull <model-name>`
> If a variable is left empty (not absent), the default is ignored and the app will return 500.

**Option 2 — Gemini (free, no GPU)**
```env
OLLAMA_ENABLED=false
GEMINI_ENABLED=true
GEMINI_API_KEY=<key from aistudio.google.com>
GEMINI_CHAT_MODEL=gemini-2.0-flash
GEMINI_EMBEDDING_MODEL=text-embedding-004
```

**Option 3 — OpenAI (paid)**
```env
OLLAMA_ENABLED=false
OPENAI_ENABLED=true
OPENAI_API_KEY=<key from platform.openai.com>
OPENAI_CHAT_MODEL=gpt-4o-mini
OPENAI_EMBEDDING_MODEL=text-embedding-3-small
```

```bash
# Build
mvn clean package

# Build (skip tests)
mvn clean package -DskipTests

# Run
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

Backend available at `http://localhost:8081`.
Swagger UI: `http://localhost:8081/swagger-ui/index.html`

## Docker

```bash
docker-compose up --build   # first run — builds images
docker-compose up           # subsequent runs — uses existing images
```

Services started: `postgres` (5432) → `app` (8081) → `frontend` (80).

![Docker containers](../docs/images/bigrun-coach%20docker%20containers.png)

## Tests

```bash
# Unit
mvn test

# Single test class
mvn test -Dtest=PaceCalculatorToolTest

# Integration (TestContainers + WireMock)
mvn verify -P integration-tests -Dskip.surefire.tests=true

# BDD — Cucumber + Gherkin
mvn verify -P cucumber -Dskip.surefire.tests=true

# Live AI (requires Ollama running locally)
mvn verify -P live-ai -Dskip.surefire.tests=true

# All (unit + integration + BDD)
mvn verify -P integration-tests,cucumber
```

> Integration and BDD tests mock `KnowledgeLoader` via `@MockitoBean` — no Ollama connection needed at startup.
> WireMock stubs (`ollama-chat-response.json`, `ollama-embed-response.json`) replace Ollama HTTP calls during tests.

![Daily pipeline](../docs/images/bigrun-coach-daily.png)
![Daily Allure report](../docs/images/bigrun-coach-daily%20allure%20report.png)

## Package structure

```
pl.bigrun.bigruncoach
├── controller/     ChatController, ConfigController
├── service/        ChatServiceImpl (RAG + Tools), RagService, ConfigServiceImpl
├── tool/           PaceCalculatorTool (5 methods), NutritionTool (2 methods)
├── config/         AppConfig, KnowledgeLoader
├── dto/            ChatRequest, ChatResponse, ConfigResponse, ErrorResponse
└── exception/      GlobalExceptionHandler
```

## Key files

| File | Purpose |
|------|---------|
| `src/main/resources/prompt-template.st` | StringTemplate: `{context}` + `{question}` |
| `src/main/resources/knowledge/*.md` | RAG knowledge base (5 documents) |
| `src/main/resources/application.yaml` | Spring config, AI providers, RAG params |
| `.env.example` | All configurable variables with defaults |
| `Jenkinsfile` | Daily CI pipeline |
| `Jenkinsfile.nightly` | Nightly pipeline + Live AI |
| `Jenkinsfile.e2e` | Selenium E2E pipeline |

> Pipelines run on a local Jenkins instance — portfolio project without public CI infrastructure.
