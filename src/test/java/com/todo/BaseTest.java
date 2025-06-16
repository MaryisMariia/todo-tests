package com.todo;

import com.todo.requests.TodoBaseRequest;
import com.todo.requests.search.SearchRequest;
import com.todo.specs.RequestSpec;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import com.todo.models.Todo;
import com.todo.respvalidators.ValidatedBaseResponse;

public class BaseTest {

    RequestSpec requestSpec = new RequestSpec(ContentType.JSON);

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    protected Response createTodo(Todo todo) {
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());

        // Отправляем POST запрос для создания нового TODO
        return todoRequest.create(todo);
    }

    protected void deleteAllTodos() {

        Todo[] todos = getAllTodos();

        for (Todo todo : todos) {
            RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
            new ValidatedBaseResponse(new TodoBaseRequest(requestSpec.authSpec())
                    .delete(todo.getId()))
                    .assertStatusCode(HttpStatus.SC_NO_CONTENT);
        }
    }

    protected Todo[] getAllTodos() {
        SearchRequest searchRequest = new SearchRequest(requestSpec.authSpec());
        Response response = searchRequest.readAll();
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_OK);
        return validatedBaseResponse.extractResponse(Todo[].class);
    }
}
