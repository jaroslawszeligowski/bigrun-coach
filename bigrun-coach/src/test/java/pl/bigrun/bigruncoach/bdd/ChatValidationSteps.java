/**
 * Project: bigrun-coach
 * File: ChatValidationSteps
 * ------------------------------------------------------------------------
 * Description:
 * Cucumber step definitions for chat validation scenarios.
 * Mirrors ChatValidationIT scenarios in Gherkin — no changes to ChatValidationIT.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.bigrun.bigruncoach.config.KnowledgeLoader;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ChatValidationSteps {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @LocalServerPort
    int port;

    @MockitoBean
    KnowledgeLoader knowledgeLoader;

    private RequestSpecification spec;
    private Response response;

    @Before
    public void setUp() {
        spec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(JSON)
                .build();
    }

    @Given("aplikacja działa")
    public void aplikacjaDziala() {}

    @When("wysyłam POST z body {}")
    public void wysylamPostZBody(String body) {
        response = given(spec).body(body).when().post("/api/v1/chat");
    }

    @When("wysyłam POST z Content-Type {string} i body {string}")
    public void wysylamPostZContentType(String contentType, String body) {
        response = given(spec).contentType(contentType).body(body).when().post("/api/v1/chat");
    }

    @When("wysyłam GET na \\/api\\/v1\\/chat")
    public void wysylamGet() {
        response = given(spec).body("{\"message\": \"test\"}").when().get("/api/v1/chat");
    }

    @Then("status odpowiedzi to {int}")
    public void statusOdpowiedziTo(int status) {
        response.then().statusCode(status);
    }

    @And("odpowiedź zawiera pole status równe {int}")
    public void odpowiedzZawieraPoleStatus(int status) {
        response.then().body("status", equalTo(status));
    }

    @And("odpowiedź zawiera niepuste pole message")
    public void odpowiedzZawieraNiepustePoleMessage() {
        response.then().body("message", not(emptyString()));
    }
}
