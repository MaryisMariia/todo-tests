package com.todo.post;

import com.todo.asserts.TodoExistenceAssert;
import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.TodoBaseRequest;
import com.todo.respvalidators.ValidatedBaseResponse;
import com.todo.specs.RequestSpec;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PostTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    public void testCreateTodoWithValidData() {
        Todo newTodo = new Todo(1, "New Task", false);

        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());

        // Отправляем POST запрос для создания нового TODO
        Response resp = todoRequest.create(newTodo);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_CREATED);
        // Проверяем, что тело ответа пустое
        assertNull(validatedBaseResponse.extractResponse(Object.class));

        // Проверяем, что TODO было успешно создано
        TodoExistenceAssert.assertTodoExistence(newTodo);
    }

    /**
     * TC2: Попытка создания TODO с отсутствующими обязательными полями.
     */
    @Test
    public void testCreateTodoWithMissingFields() {
        // Создаем JSON без обязательного поля 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());
        Response resp = todoRequest.create(invalidTodoJson);

        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_BAD_REQUEST);
        // Проверяем, что есть сообщение об ошибке
        assertNotNull(validatedBaseResponse.extractResponse(String.class));
    }

    /**
     * TC3: Создание TODO с максимально допустимой длиной поля 'text'.
     */
    @Test
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = new Todo(3, maxLengthText, false);

        // Отправляем POST запрос для создания нового TODO
        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());
        Response resp = todoRequest.create(newTodo);

        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_CREATED);
        assertNull(validatedBaseResponse.extractResponse(String.class));

        // Проверяем, что TODO было успешно создано
        TodoExistenceAssert.assertTodoExistence(newTodo);
    }

    /**
     * TC4: Передача некорректных типов данных в полях.
     */
    @Test
    public void testCreateTodoWithInvalidDataTypes() {
        // Поле 'completed' содержит строку вместо булевого значения
        Todo newTodo = new Todo(3, "djjdjd", false);

        RequestSpec requestSpec = new RequestSpec(ContentType.TEXT);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());
        Response resp = todoRequest.create(newTodo);

        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_BAD_REQUEST);
        // Проверяем, что есть сообщение об ошибке
        assertNotNull(validatedBaseResponse.extractResponse(String.class));
    }

    /**
     * TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).
     */
    @Test
    public void testCreateTodoWithExistingId() {
        // Сначала создаем TODO с id = 5
        Todo firstTodo = new Todo(5, "First Task", false);

        RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
        TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());
        todoRequest.create(firstTodo);

        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = new Todo(5, "Duplicate Task", true);
        Response resp = todoRequest.create(duplicateTodo);

        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(resp);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_BAD_REQUEST);
        // Проверяем, что есть сообщение об ошибке
        assertNotNull(validatedBaseResponse.extractResponse(String.class));
    }

}
