package com.testproject.todo.post;

import com.testproject.todo.BaseTest;
import com.todo.responses.IncorrectDataResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static com.todo.generators.DataGenerator.generateTodo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.todo.models.Todo;

@Epic("TODO Management")
@Feature("Post Todos API")
public class PostTodosTests extends BaseTest {

    @Test
    @Description("Авторизованный юзер может создать todo")
    public void testCreateTodoWithValidData() {
        Todo newTodo = generateTodo();
        softly.assertThat(todoRequester.getValidatedRequest().create(newTodo))
                .isEmpty();
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(newTodo);
    }

    @Test
    @Description("Авторизованный юзер может создать todo с максимально допустимой длиной поля 'text'")
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = generateTodo();
        newTodo.setText(maxLengthText);

        softly.assertThat(todoRequester.getValidatedRequest().create(newTodo))
                .isEmpty();
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(newTodo);
    }

    @Test
    @Description("Авторизованный юзер не может создать todo с уже существующим 'id' (если 'id' задается клиентом)")
    public void testCreateTodoWithExistingId() {
        // Сначала создаем TODO с id = 5
        Todo firstTodo = generateTodo();
        todoRequester.getValidatedRequest().create(firstTodo);

        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = generateTodo();
        duplicateTodo.setId(firstTodo.getId());

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .assertThat()
                .spec(new IncorrectDataResponse().sameId(firstTodo.getId()));
    }
}
