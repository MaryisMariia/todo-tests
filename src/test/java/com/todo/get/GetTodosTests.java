package com.todo.get;


import com.todo.BaseTest;
import com.todo.requests.search.SearchRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.todo.asserts.TodoAttributesAssert.assertTodoAttributes;
import static com.todo.constants.ErrorMessages.INVALID_QUERY_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.todo.models.Todo;
import com.todo.respvalidators.ValidatedBaseResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Epic("TODO Management")
@Feature("Get Todos API")
public class GetTodosTests extends BaseTest {

    private RequestSpec requestSpec = new RequestSpec(ContentType.JSON);
    private SearchRequest searchRequest = new SearchRequest(requestSpec.unauthSpec());

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Description("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {
        Todo[] todos = getAllTodos();
        int lengthOfEmptyArray = 0;
        assertEquals(lengthOfEmptyArray, todos.length);
    }

    @Test
    @Description("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        // Предварительно создать несколько TODO
        Todo todo1 = new Todo(1, "Task 1", false);
        Todo todo2 = new Todo(2, "Task 2", true);

        createTodo(todo1);
        createTodo(todo2);

        Todo[] todos = getAllTodos();
        int lengthOfArray = 2;
        assertEquals(lengthOfArray, todos.length);

        assertTodoAttributes(todo1, todos[0]);
        assertTodoAttributes(todo2, todos[1]);
    }

    @ParameterizedTest
    @CsvSource({"2, 2"})
    @Description("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit(int offset, int limit) {
        // Создаем 5 TODO
        for (int i = 1; i <= 5; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }

        Response response = searchRequest.readAll(offset, limit);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_OK);
        Todo[] todos = validatedBaseResponse.extractResponse(Todo[].class);
        int lengthOfArray = 2;
        assertEquals(lengthOfArray, todos.length);

        // Проверяем, что получили задачи с id 3 и 4
        assertEquals(3, todos[0].getId());
        assertEquals("Task 3", todos[0].getText());

        assertEquals(4, todos[1].getId());
        assertEquals("Task 4", todos[1].getText());
    }

    @ParameterizedTest
    @CsvSource({"-1, 2", "0, abc", ", 2"})
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit(Object offset, Object limit) {
        // Тест с отрицательным offset
        requestSpec = new RequestSpec(ContentType.ANY);
        searchRequest = new SearchRequest(requestSpec.unauthSpec());

        Response response = searchRequest.readAll(offset, limit);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_BAD_REQUEST);
        assertTrue(validatedBaseResponse.extractErrorResponse().contains(INVALID_QUERY_STRING));
    }

    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        // Создаем 10 TODO
        int allCount = 10;
        for (int i = 1; i <= allCount; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }
        int maxLimit = 1000;
        Response response = searchRequest.readAll(null, maxLimit);
        ValidatedBaseResponse validatedBaseResponse = new ValidatedBaseResponse(response);
        validatedBaseResponse.assertStatusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
