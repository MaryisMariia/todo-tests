package com.todo.put;

import com.todo.asserts.TodoExistenceAssert;
import com.todo.BaseTest;
import com.todo.requests.TodoBaseRequest;
import com.todo.specs.RequestSpec;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.todo.models.Todo;
import com.todo.respvalidators.ValidatedBaseResponse;

public class PutTodosTests extends BaseTest {

    private final RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
    private final TodoBaseRequest todoRequest = new TodoBaseRequest(requestSpec.unauthSpec());

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    /**
     * TC1: Обновление существующего TODO корректными данными.
     */
    @Test
    public void testUpdateExistingTodoWithValidData() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(1, "Original Task", false);
        createTodo(originalTodo);

        // Обновленные данные
        Todo updatedTodo = new Todo(1, "Updated Task", true);

        // Отправляем PUT запрос для обновления
        Response response = todoRequest.update(updatedTodo.getId(), updatedTodo);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_OK);

        // Проверяем, что данные были обновлены
        TodoExistenceAssert.assertTodoExistence(updatedTodo);
    }

    /**
     * TC2: Попытка обновления TODO с несуществующим id.
     */
    @Test
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        Todo updatedTodo = new Todo(999, "Non-existent Task", true);

        Response response = todoRequest.update(updatedTodo.getId(), updatedTodo);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_NOT_FOUND);
        assertNotNull(validatedBaseResponse.extractErrorResponse());
    }

    /**
     * TC3: Обновление TODO с отсутствием обязательных полей.
     */
    @Test
    public void testUpdateTodoWithMissingFields() {
        long id = 2;
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(id, "Task to Update", false);
        createTodo(originalTodo);

        // Обновленные данные с отсутствующим полем 'text'
        String invalidTodoJson = "{ \"id\": " + id + ", \"completed\": true }";

        Response response = todoRequest.update(id, invalidTodoJson);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_UNAUTHORIZED);
        assertNotNull(validatedBaseResponse.extractErrorResponse());
    }

    /**
     * TC4: Передача некорректных типов данных при обновлении.
     */
    @Test
    public void testUpdateTodoWithInvalidDataTypes() {
        // Создаем TODO для обновления
        long id = 3;
        Todo originalTodo = new Todo(id, "Another Task", false);
        createTodo(originalTodo);

        // Обновленные данные с некорректным типом поля 'completed'
        String invalidTodoJson = "{ \"id\": " + id + ", \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

        Response response = todoRequest.update(id, invalidTodoJson);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    /**
     * TC5: Обновление TODO без изменения данных (передача тех же значений).
     */
    @Test
    public void testUpdateTodoWithoutChangingData() {
        // Создаем TODO для обновления
        long id = 4;
        Todo originalTodo = new Todo(id, "Task without Changes", false);
        createTodo(originalTodo);

        // Отправляем PUT запрос с теми же данными
        Response response = todoRequest.update(id, originalTodo);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_OK);

        // Проверяем, что данные не изменились
        TodoExistenceAssert.assertTodoExistence(originalTodo);
    }
}
