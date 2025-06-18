package com.todo.delete;

import com.todo.BaseTest;

import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.todo.models.Todo;

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
        Todo todo = generateTestData(Todo.class);
        createTodo(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        given()
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic("admin", "admin")
                .when()
                .delete("/todos/" + todo.getId())
                .then()
                .statusCode(204)
                .body(is(emptyOrNullString())); // Проверяем, что тело ответа пустое

        // Получаем список всех TODO и проверяем, что удаленная задача отсутствует
        todoExistenceAssert.assertTodoNonExistence(todo);
    }

    /**
     * TC2: Попытка удаления TODO без заголовка Authorization.
     */
    @Test
    public void testDeleteTodoWithoutAuthHeader() {
        // Создаем TODO для удаления
        Todo todo = generateTestData(Todo.class);
        createTodo(todo);

        // Отправляем DELETE запрос без заголовка Authorization
        given()
                .filter(new AllureRestAssured())
                .when()
                .delete("/todos/" + todo.getId())
                .then()
                .statusCode(401);
        //.contentType(ContentType.JSON)
        //.body("error", notNullValue()); // Проверяем наличие сообщения об ошибке

        // Проверяем, что TODO не было удалено
        todoExistenceAssert.assertTodoExistence(todo);
    }

    /**
     * TC3: Попытка удаления TODO с некорректными учетными данными.
     */
    @Test
    public void testDeleteTodoWithInvalidAuth() {
        // Создаем TODO для удаления
        Todo todo = generateTestData(Todo.class);
        createTodo(todo);

        // Отправляем DELETE запрос с некорректной авторизацией
        given()
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic("invalidUser", "invalidPass")
                .when()
                .delete("/todos/" + todo.getId())
                .then()
                .statusCode(401);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());

        // Проверяем, что TODO не было удалено
        todoExistenceAssert.assertTodoExistence(todo);
    }

    /**
     * TC4: Удаление TODO с несуществующим id.
     */
    @Test
    public void testDeleteNonExistentTodo() {
        // Отправляем DELETE запрос для несуществующего TODO с корректной авторизацией
        given()
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic("admin", "admin")
                .when()
                .delete("/todos/999")
                .then()
                .statusCode(404);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());

        // Дополнительно можем проверить, что список TODO не изменился
        Todo[] todos = given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        // В данном случае, поскольку мы не добавляли задач с id 999, список должен быть пуст или содержать только ранее добавленные задачи
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
