package com.testproject.todo.put;

import com.testproject.todo.BaseTest;
import com.todo.models.TodoBuilder;
import com.todo.responses.IncorrectDataResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static com.todo.generators.DataGenerator.generateString;
import static com.todo.generators.DataGenerator.generateTodo;

import com.todo.models.Todo;

@Epic("TODO Management")
@Feature("Post Todos API")
public class PutTodosTests extends BaseTest {

    @Test
    @Description("Авторизованный юзер может обновить todo")
    public void testUpdateExistingTodoWithValidData() {
        // Создаем TODO для обновления
        Todo originalTodo = generateTodo();
        todoRequester.getValidatedRequest().create(originalTodo);

        Todo updatedTodo = new TodoBuilder()
                .setId(originalTodo.getId())
                .setText(generateString())
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
        Todo updatedTodo = generateTodo();

        todoRequester.getRequest().update(updatedTodo.getId(), updatedTodo)
                .then()
                .assertThat().spec(IncorrectDataResponse.nonExistingId(updatedTodo.getId()));
    }

    @Test
    @Description("Авторизованный юзер может обновить todo без изменения данных (передача тех же значений)")
    public void testUpdateTodoWithoutChangingData() {

        // Создаем TODO для обновления
        Todo originalTodo = generateTodo();
        todoRequester.getValidatedRequest().create(originalTodo);

        todoRequester.getValidatedRequest().update(originalTodo.getId(), originalTodo);

        // Проверяем, что данные не были обновлены
        softly.assertThat(todoRequester.getValidatedRequest().readAll()).containsExactly(originalTodo);
    }
}
