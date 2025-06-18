package com.todo.put;

import com.todo.BaseTest;
import com.todo.models.TodoBuilder;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateRandomString;
import static com.todo.generators.TestDataGenerator.generateTestData;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.todo.models.Todo;

public class PutTodosTests extends BaseTest {

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
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);

        // Обновленные данные
        int textLength = 10;
        Todo updatedTodo = new TodoBuilder()
                .setId(originalTodo.getId())
                .setText(generateRandomString(textLength))
                .setCompleted(originalTodo.isCompleted())
                .build();

        // Отправляем PUT запрос для обновления
        given()
                .filter(new ResponseLoggingFilter())
                .contentType(ContentType.JSON)
                .body(updatedTodo)
                .when()
                .put("/todos/" + updatedTodo.getId())
                .then()
                .statusCode(200);
        //.contentType(ContentType.JSON)
//                .body("id", equalTo(1))
//                .body("text", equalTo("Updated Task"))
//                .body("completed", equalTo(true));

        // Проверяем, что данные были обновлены
        Todo[] todos = given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        softAssertions.assertThat(updatedTodo).isEqualTo(todos[0]);
    }

    /**
     * TC2: Попытка обновления TODO с несуществующим id.
     */
    @Test
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        long nonExistedId = 999;
        Todo updatedTodo = generateTestData(Todo.class);
        updatedTodo.setId(nonExistedId);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(updatedTodo)
                .when()
                .put("/todos/" + updatedTodo.getId())
                .then()
                .statusCode(404)
                //.contentType(ContentType.TEXT)
                .body(is(notNullValue()));
    }

    // This test is not part of functional testing.
//    /**
//     * TC3: Обновление TODO с отсутствием обязательных полей.
//     */
//    @Test
//    public void testUpdateTodoWithMissingFields() {
//        // Создаем TODO для обновления
//        Todo originalTodo = new Todo(2, "Task to Update", false);
//        createTodo(originalTodo);
//
//        // Обновленные данные с отсутствующим полем 'text'
//        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";
//
//        given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .body(invalidTodoJson)
//                .when()
//                .put("/todos/2")
//                .then()
//                .statusCode(401);
//        //.contentType(ContentType.JSON)
//        //.body("error", containsString("Missing required field 'text'"));
//    }
//
//    /**
//     * TC4: Передача некорректных типов данных при обновлении.
//     */
//    @Test
//    public void testUpdateTodoWithInvalidDataTypes() {
//        // Создаем TODO для обновления
//        Todo originalTodo = new Todo(3, "Another Task", false);
//        createTodo(originalTodo);
//
//        // Обновленные данные с некорректным типом поля 'completed'
//        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";
//
//        given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .body(invalidTodoJson)
//                .when()
//                .put("/todos/3")
//                .then()
//                .statusCode(401);
//    }

    /**
     * TC5: Обновление TODO без изменения данных (передача тех же значений).
     */
    @Test
    public void testUpdateTodoWithoutChangingData() {
        // Создаем TODO для обновления
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);

        // Отправляем PUT запрос с теми же данными
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(originalTodo)
                .when()
                .put("/todos/4")
                .then()
                .statusCode(200);


        // Проверяем, что данные не изменились
        Todo[] todo = given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        softAssertions.assertThat(originalTodo).isEqualTo(todo[0]);
    }
}
