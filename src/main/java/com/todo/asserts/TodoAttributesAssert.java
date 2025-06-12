package com.todo.asserts;

import com.todo.models.Todo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoAttributesAssert {

    public static void assertTodoAttributes(Todo expectedTodo, Todo actualTodo) {
        assertEquals(expectedTodo.getId(), actualTodo.getId());
        assertEquals(expectedTodo.getText(), actualTodo.getText());
        assertEquals(expectedTodo.isCompleted(), actualTodo.isCompleted());
    }
}
