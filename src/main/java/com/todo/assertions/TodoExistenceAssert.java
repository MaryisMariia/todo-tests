package com.todo.assertions;

import com.todo.models.Todo;
import com.todo.requests.TodoRequester;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

public class TodoExistenceAssert {

    private final TodoRequester todoRequester;
    private final SoftAssertions softAssertions;

    public TodoExistenceAssert(TodoRequester todoRequester, SoftAssertions softAssertions) {
        this.todoRequester = todoRequester;
        this.softAssertions = softAssertions;
    }

    public void assertTodoExistence(Todo newTodo) {
        boolean found = doesTodoExist(newTodo);
        softAssertions.assertThat(found)
                .withFailMessage("Созданная задача не найдена в списке TODO")
                .isEqualTo(true);
    }

    public void assertTodoNonExistence(Todo newTodo) {
        boolean found = doesTodoExist(newTodo);
        softAssertions.assertThat(found)
                .withFailMessage("Удаленная задача все еще присутствует в списке TODO")
                .isEqualTo(false);
    }

    private boolean doesTodoExist(Todo newTodo) {
        List<Todo> todos = todoRequester.getValidatedRequest().readAll();
        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                softAssertions.assertThat(newTodo.getText()).isEqualTo(todo.getText());
                softAssertions.assertThat(newTodo.isCompleted()).isEqualTo(todo.isCompleted());
                found = true;
                break;
            }
        }
        return found;
    }
}
