package com.todo.post;

import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostTodosTests extends BaseTest {
    @Test
    @Description("Авторизованный юзер может создать todo")
    public void testCreateTodoWithValidData() {
        Todo newTodo = generateTestData(Todo.class);
        assertTrue(todoRequester.getValidatedRequest().create(newTodo)
                .isEmpty());
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(newTodo);
    }

    //контрактное тестирование
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

    @Test
    @Description("Авторизованный юзер может создать todo с максимально допустимой длиной поля 'text'")
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = generateTestData(Todo.class);
        newTodo.setText(maxLengthText);

        softly.assertThat(todoRequester.getValidatedRequest().create(newTodo))
                .isEmpty();
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(newTodo);
    }

    //контрактное тестирование
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

    @Test
    @Description("Авторизованный юзер не может создать todo с уже существующим 'id' (если 'id' задается клиентом)")
    public void testCreateTodoWithExistingId() {
        // Сначала создаем TODO с id = 5
        Todo firstTodo = new Todo(5, "First Task", false);

        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = new Todo(5, "Duplicate Task", true);

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .assertThat()
                .spec(new IncorrectDataResponse().sameId(firstTodo.getId()));
    }

}
