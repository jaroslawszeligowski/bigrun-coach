# bigrun-coach-se — E2E Tests

Selenium 4 + JUnit 5 E2E test suite for BigRun Coach UI.
See the [root README](../README.md) for architecture overview.

## Requirements

- Java 21, Maven 3.9
- Running BigRun Coach stack (`bigrun-coach/docker-compose up`)
- Docker Desktop (for Selenium Grid)

## Run against local browser

```bash
mvn test -Dbrowser=chrome -Dbase.url=http://localhost
mvn test -Dbrowser=firefox -Dbase.url=http://localhost

# Single test class
mvn test -Dbrowser=chrome -Dbase.url=http://localhost -Dtest=ChatSmokeE2ETest

# Clean run
mvn clean test -Dbrowser=chrome -Dbase.url=http://localhost
```

## Run against Selenium Grid

```bash
# Start Grid
docker-compose up -d

# Run tests
mvn test -Dbrowser=chrome \
         -Dgrid.url=http://localhost:4444 \
         -Dbase.url=http://localhost
```

Grid services: Hub (4444) · Chrome · Firefox

## Selenium Grid — Docker

```bash
docker-compose up -d    # starts Hub + Chrome + Firefox nodes
docker-compose down     # stops and removes containers
```

Grid UI available at `http://localhost:4444`.

## Test classes

| Class | Scenarios |
|-------|-----------|
| `ChatSmokeE2ETest` | page loads, title visible |
| `ChatFlowE2ETest` | send message, verify response appears |
| `ChatExampleQueriesE2ETest` | parametrized — clicks example queries |
| `ChatModelGenericE2ETest` | model selector buttons work |
| `ChatModelOllamaE2ETest` | Ollama-specific (skipped if provider ≠ Ollama) |
| `ChatValidationE2ETest` | empty message → error handling |

`ChatPage` — Page Object Model with all Selenium locators and helper methods.

![E2E pipeline](../docs/images/bigrun-coach-e2e.png)

## Allure reports

```bash
mvn test
mvn allure:report   # generates report in target/site/allure-maven-plugin/
mvn allure:serve    # generates + opens report in browser
```
