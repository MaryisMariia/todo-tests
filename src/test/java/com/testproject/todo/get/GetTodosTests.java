package com.testproject.todo.get;


import com.testproject.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.responses.IncorrectDataResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("TODO Management")
@Feature("Get Todos API")
@ExtendWith(DataPreparationExtension.class)
public class GetTodosTests extends BaseTest {

    @Test
    @PrepareTodo(5)
    @Description("Авторизованный юзер может получить список всех todo")
    public void testGetTodosWithExistingEntries() {
        var createdTodos = todoRequester.getValidatedRequest().readAll();

        softly.assertThat(createdTodos).hasSize(5);
    }

    @Test
    @PrepareTodo(5)
    @Description("Авторизованный юзер может получать список todo с учетом offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        var createdTodos = todoRequester.getValidatedRequest().readAll(2, 2);

        softly.assertThat(createdTodos).hasSize(2);
    }

    @Test
    @DisplayName("Авторизованный юзер не может передать некорректные значения в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        todoRequester.getRequest().readAll(-1, -1)
                .then().assertThat().spec(IncorrectDataResponse.offsetOrLimitHaveIncorrectValues());
    }

    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        var paginatedTodos = todoRequester.getValidatedRequest().readAll(1, 1000);
        var allTodos = todoRequester.getValidatedRequest().readAll();

        softly.assertThat(paginatedTodos).isEqualTo(allTodos);
    }
}
