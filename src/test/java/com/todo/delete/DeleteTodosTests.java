package com.todo.delete;

import com.todo.asserts.TodoExistenceAssert;
import com.todo.BaseTest;

import com.todo.requests.TodoBaseRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import com.todo.models.Todo;
import com.todo.respvalidators.ValidatedBaseResponse;

public class DeleteTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    /**
     * TC1: Успешное удаление существующего TODO с корректной авторизацией.
     */
    @Test
    public void testDeleteExistingTodoWithValidAuth() {
        // Создаем TODO для удаления
        Todo todo = new Todo(1, "Task to Delete", false);
        createTodo(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.authSpec());

        // Отправляем POST запрос для создания нового TODO
        Response resp = todoRequest.delete(todo.getId());
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_NO_CONTENT);
        // Проверяем, что тело ответа пустое
        assertTrue(validatedBaseResponse.extractErrorResponse().isEmpty());

        // проверяем, что удаленная задача отсутствует
        TodoExistenceAssert.assertTodoNonExistence(todo);
    }

    /**
     * TC2: Попытка удаления TODO без заголовка Authorization.
     */
    @Test
    public void testDeleteTodoWithoutAuthHeader() {
        // Создаем TODO для удаления
        Todo todo = new Todo(2, "Task to Delete", false);
        createTodo(todo);

        // Отправляем DELETE запрос без заголовка Authorization
        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());

        // Отправляем POST запрос для создания нового TODO
        Response resp = todoRequest.delete(todo.getId());
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_UNAUTHORIZED);

        // Проверяем, что TODO не было удалено
        TodoExistenceAssert.assertTodoExistence(todo);
    }

    /**
     * TC3: Попытка удаления TODO с некорректными учетными данными.
     */
    @Test
    public void testDeleteTodoWithInvalidAuth() {
        // Создаем TODO для удаления
        Todo todo = new Todo(3, "Task to Delete", false);
        createTodo(todo);

        // Отправляем DELETE запрос с некорректной авторизацией
        String invalidUsername = "invalidUser";
        String invalidPassword = "invalidPass";
        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.authSpec(invalidUsername, invalidPassword));

        // Отправляем POST запрос для создания нового TODO
        Response resp = todoRequest.delete(todo.getId());
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_UNAUTHORIZED);

        // Проверяем, что TODO не было удалено
        TodoExistenceAssert.assertTodoExistence(todo);
    }

    /**
     * TC4: Удаление TODO с несуществующим id.
     */
    @Test
    public void testDeleteNonExistentTodo() {
        // Отправляем DELETE запрос для несуществующего TODO с корректной авторизацией
        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.authSpec());

        long nonExistedId = 999;
        Response resp = todoRequest.delete(nonExistedId);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_NOT_FOUND);

        // Дополнительно можем проверить, что список TODO не изменился
        Todo[] todos = getAllTodos();

        // В данном случае, поскольку мы не добавляли задач с id 999, список должен быть пуст или содержать только ранее добавленные задачи
        assertEquals(0, todos.length);
    }

    /**
     * TC5: Попытка удаления с некорректным форматом id (например, строка вместо числа).
     */
    @Test
    public void testDeleteTodoWithInvalidIdFormat() {
        // Отправляем DELETE запрос с некорректным id
        given()
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic("admin", "admin")
                .when()
                .delete("/todos/invalidId")
                .then()
                .statusCode(404);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());
    }
}
