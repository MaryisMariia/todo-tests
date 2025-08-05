package com.todo.generators;

import com.todo.models.Todo;
import com.todo.models.TodoBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class DataGenerator {

    public static Todo generateTodo() {
        return new TodoBuilder()
                .setId(Long.parseLong(RandomStringUtils.randomNumeric(3)))
                .setText(RandomStringUtils.randomAlphabetic(1, 256))
                .setCompleted(RandomUtils.nextBoolean())
                .build();
    }

    public static String generateString() {
        return RandomStringUtils.randomAlphabetic(1, 256);
    }
}
