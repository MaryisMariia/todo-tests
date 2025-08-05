package com.testproject.todo.delete;

import com.testproject.todo.BaseTest;

import com.todo.annotations.PrepareTodo;
import com.todo.requests.TodoRequest;
import com.todo.responses.AccessErrorResponse;
import com.todo.responses.IncorrectDataResponse;
import com.todo.specs.RequestSpec;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;
import com.todo.models.Todo;

import java.util.Random;

@Epic("TODO Management")
@Feature("Delete Todos API")
public class DeleteTodosTests extends BaseTest {

    @Test
    @PrepareTodo(1)
    @Description("Авторизированный юзер может удалить todo")
    public void testDeleteExistingTodoWithValidAuth() {
        Todo createdTodo = todoRequester.getValidatedRequest().readAll().getFirst();

        todoRequester.getValidatedRequest().delete(createdTodo.getId());

        softly.assertThat(todoRequester.getValidatedRequest().readAll()).hasSize(0);
    }

    @Test
    @PrepareTodo(1)
    @Description("Неавторизированный юзер не может удалить todo")
    public void testDeleteTodoWithoutAuthHeader() {
        Todo createdTodo = todoRequester.getValidatedRequest().readAll().getFirst();

        new TodoRequest(RequestSpec.unauthSpec()).delete(createdTodo.getId())
                .then().assertThat().spec(AccessErrorResponse.userIsUnauthorized());

        softly.assertThat(todoRequester.getValidatedRequest().readAll()).hasSize(1);
    }

    @Test
    @Description("Авторизованный юзер не может удалить todo с несуществующим id")
    public void testDeleteNonExistentTodo() {
        var nonExistingId = new Random().nextInt();
        todoRequester.getRequest().delete(nonExistingId)
                .then().assertThat().spec(IncorrectDataResponse.nonExistingId(nonExistingId));
    }
}
