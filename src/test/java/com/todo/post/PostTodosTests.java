package com.todo.post;

import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PostTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    public void testCreateTodoWithValidData() {
        Todo newTodo = generateTestData(Todo.class);
        // Отправляем POST запрос для создания нового TODO
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(newTodo)
                .when()
                .post("/todos")
                .then()
                .statusCode(201)
                .body(is(emptyOrNullString())); // Проверяем, что тело ответа пустое

        // Проверяем, что TODO было успешно создано
        Todo[] todos = given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        // Ищем созданную задачу в списке
        todoExistenceAssert.assertTodoExistence(newTodo);
    }

    // This test is not part of functional testing.
//    /**
//     * TC2: Попытка создания TODO с отсутствующими обязательными полями.
//     */
//    @Test
//    public void testCreateTodoWithMissingFields() {
//        // Создаем JSON без обязательного поля 'text'
//        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";
//
//        given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .body(invalidTodoJson)
//                .when()
//                .post("/todos")
//                .then()
//                .statusCode(400)
//                .contentType(ContentType.TEXT)
//                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
//    }

    /**
     * TC3: Создание TODO с максимально допустимой длиной поля 'text'.
     */
    @Test
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = generateTestData(Todo.class);
        newTodo.setText(maxLengthText);

        // Отправляем POST запрос для создания нового TODO
        todoRequester.getValidatedRequest().create(newTodo);

        // Проверяем, что TODO было успешно создано
        List<Todo> todos = todoRequester.getValidatedRequest().readAll();

        // Ищем созданную задачу в списке
        todoExistenceAssert.assertTodoExistence(newTodo);
    }

    // This test is not part of functional testing.
//    /**
//     * TC4: Передача некорректных типов данных в полях.
//     */
//    @Test
//    public void testCreateTodoWithInvalidDataTypes() {
//        // Поле 'completed' содержит строку вместо булевого значения
//        Todo newTodo = new TodoBuilder()
//                .setText("text")
//                .build();
//
//        todoRequester.getRequest().create(newTodo)
//                .then()
//                .statusCode(400)
//                .contentType(ContentType.TEXT)
//                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
//    }

    /**
     * TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).
     */
    @Test
    public void testCreateTodoWithExistingId() {
        // Сначала создаем TODO с id = 5
        Todo firstTodo = generateTestData(Todo.class);
        createTodo(firstTodo);

        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = generateTestData(Todo.class);
        duplicateTodo.setId(firstTodo.getId());

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .spec(new IncorrectDataResponse().sameId(firstTodo.getId()));

        // создай 100 todo
        // пометь todo как completed

        // падение
        // FAIL FIRST
    }

}
