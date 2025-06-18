package com.todo.get;


import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.Mobile;
import com.todo.annotations.MobileExecutionExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.assertions.TodoAttributesAssert;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

import com.todo.models.Todo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Epic("TODO Management")
@Feature("Get Todos API")
@ExtendWith(DataPreparationExtension.class)
@ExtendWith(MobileExecutionExtension.class)
public class GetTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Description("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(0));
    }

    public void userCanCreateTodoWithArabicText() {
        // генерируем todo
        // проставляем text=arabic
        // создаем
        // проверяем успех
    }

    @Test
    @Description("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        // Предварительно создать несколько TODO
        Todo todo1 = generateTestData(Todo.class);

        todo1.setText("arabic symbols");

        Todo todo2 = generateTestData(Todo.class);

        createTodo(todo1);
        createTodo(todo2);

        Response response =
                given()
                        .filter(new AllureRestAssured())
                        .when()
                        .get("/todos")
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body("", hasSize(2))
                        .extract().response();

        // Дополнительная проверка содержимого
        Todo[] todos = response.getBody().as(Todo[].class);
        TodoAttributesAssert todoAttributesAssert = new TodoAttributesAssert(softAssertions);
        todoAttributesAssert.assertTodoAttributes(todo1, todos[0]);
        todoAttributesAssert.assertTodoAttributes(todo2, todos[1]);
    }

    @Test
    @Mobile
    @PrepareTodo(5)
    @Description("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        List<Todo> todos = todoRequester.getValidatedRequest().readAll(2, 2);

        softAssertions.assertThat(todos.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        // Тест с отрицательным offset
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", -1)
                .queryParam("limit", 2)
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));

        // Тест с нечисловым limit
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", 0)
                .queryParam("limit", "abc")
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));

        // Тест с отсутствующим значением offset
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", "")
                .queryParam("limit", 2)
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));
    }

    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        // Создаем 10 TODO
        for (int i = 1; i <= 10; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }

        Response response =
                given()
                        .filter(new AllureRestAssured())
                        .queryParam("limit", 1000)
                        .when()
                        .get("/todos")
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .extract().response();

        Todo[] todos = response.getBody().as(Todo[].class);

        // Проверяем, что вернулось 10 задач
        softAssertions.assertThat(10).isEqualTo(todos.length);
    }
}
