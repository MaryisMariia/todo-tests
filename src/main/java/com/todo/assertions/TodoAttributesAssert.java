package com.todo.assertions;

import com.todo.models.Todo;
import org.assertj.core.api.SoftAssertions;

public class TodoAttributesAssert {
    protected SoftAssertions softAssertions;

    public TodoAttributesAssert(SoftAssertions softAssertions) {
        this.softAssertions = softAssertions;
    }

    public void assertTodoAttributes(Todo expectedTodo, Todo actualTodo) {
        softAssertions.assertThat(expectedTodo).isEqualTo(actualTodo);
    }
}
