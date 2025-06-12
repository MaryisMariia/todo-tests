package com.todo.asserts;

import com.todo.models.Todo;
import com.todo.requests.search.SearchRequest;
import com.todo.specs.RequestSpec;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import com.todo.respvalidators.ValidatedBaseResponse;

public class TodoExistenceAssert {

    private static final RequestSpec requestSpec = new RequestSpec(ContentType.JSON);

    public static void assertTodoExistence(Todo newTodo) {
        boolean found = doesTodoExist(newTodo);
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }

    public static void assertTodoNonExistence(Todo newTodo) {
        boolean found = doesTodoExist(newTodo);
        Assertions.assertFalse(found, "Удаленная задача все еще присутствует в списке TODO");
    }

    private static boolean doesTodoExist(Todo newTodo) {
        SearchRequest searchRequest = new SearchRequest(requestSpec.authSpec());
        Response response = searchRequest.readAll();
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_OK);
        Todo[] todos = validatedBaseResponse.extractResponse(Todo[].class);

        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        return found;
    }
}
