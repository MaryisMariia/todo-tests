package com.todo.put;

import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.models.TodoBuilder;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateRandomString;
import static com.todo.generators.TestDataGenerator.generateTestData;

public class PutTodosTests extends BaseTest {

    /**
     * TC1: Обновление существующего TODO корректными данными.
     */
    @Test
    @Description("Авторизованный юзер может обновить todo")
    public void testUpdateExistingTodoWithValidData() {
        // Создаем TODO для обновления
        Todo originalTodo = generateTestData(Todo.class);
        todoRequester.getValidatedRequest().create(originalTodo);

        // Обновленные данные
        int textLength = 10;
        Todo updatedTodo = new TodoBuilder()
                .setId(originalTodo.getId())
                .setText(generateRandomString(textLength))
                .setCompleted(originalTodo.isCompleted())
                .build();

        todoRequester.getValidatedRequest().update(updatedTodo.getId(), updatedTodo);

        // Проверяем, что данные были обновлены
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(updatedTodo);
    }

    @Test
    @Description("Авторизованный юзер не может обновить todo с несуществующим id")
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        Todo updatedTodo = generateTestData(Todo.class);

        todoRequester.getRequest().update(updatedTodo.getId(), updatedTodo)
                .then()
                .assertThat().spec(IncorrectDataResponse.nonExistingId(updatedTodo.getId()));
    }

    //контрактное тестирование
//    /**
//     * TC3: Обновление TODO с отсутствием обязательных полей.
//     */
//    @Test
//    public void testUpdateTodoWithMissingFields() {
//        // Создаем TODO для обновления
//        Todo originalTodo = new Todo(2, "Task to Update", false);
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

    //контрактное тестирование
//    /**
//     * TC4: Передача некорректных типов данных при обновлении.
//     */
//    @Test
//    public void testUpdateTodoWithInvalidDataTypes() {
//        // Создаем TODO для обновления
//        Todo originalTodo = new Todo(3, "Another Task", false);
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

    @Test
    @Description("Авторизованный юзер может обновить todo без изменения данных (передача тех же значений)")
    public void testUpdateTodoWithoutChangingData() {

        // Создаем TODO для обновления
        Todo originalTodo = generateTestData(Todo.class);
        todoRequester.getValidatedRequest().create(originalTodo);

        todoRequester.getValidatedRequest().update(originalTodo.getId(), originalTodo);

        // Проверяем, что данные не были обновлены
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(originalTodo);
    }
}
