package com.todo;

import com.todo.assertions.TodoExistenceAssert;
import com.todo.conf.Configuration;
import com.todo.requests.TodoRequest;
import com.todo.requests.TodoRequester;
import com.todo.specs.request.RequestSpec;
import com.todo.storages.TestDataStorage;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import com.todo.models.Todo;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseTest {
    protected TodoRequester todoRequester;
    protected SoftAssertions softAssertions;
    protected TodoExistenceAssert todoExistenceAssert;

    @BeforeAll
    public static void setup() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = Configuration.getProperty("baseUrl");
        RestAssured.port = 8080;
    }

    @BeforeEach
    public void setupTest() {
        todoRequester = new TodoRequester(RequestSpec.authSpec());
        softAssertions = new SoftAssertions();
        todoExistenceAssert = new TodoExistenceAssert(todoRequester, softAssertions);
    }

    protected void createTodo(Todo todo) {
        given()
                .contentType("application/json")
                .body(todo)
                .when()
                .post("/todos")
                .then()
                .statusCode(201);
    }

    protected void deleteAllTodos() {

        Todo[] todos = given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        for (Todo todo : todos) {
            given()
                    .auth()
                    .preemptive()
                    .basic("admin", "admin")
                    .when()
                    .delete("/todos/" + todo.getId())
                    .then()
                    .statusCode(204);
        }
    }

    @AfterEach
    public void clean() {
        TestDataStorage.getInstance().getStorage()
                .forEach((k, v) ->
                        new TodoRequest(RequestSpec.authSpec())
                                .delete(k));

        TestDataStorage.getInstance().clean();

        softAssertions.assertAll();
    }
}
