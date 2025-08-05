package com.todo.annotations;

import com.todo.requests.TodoRequest;
import com.todo.specs.RequestSpec;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.todo.generators.DataGenerator.generateTodo;

public class DataPreparationExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        var testMethod = extensionContext.getRequiredTestMethod();

        var prepareTodo = testMethod.getAnnotation(PrepareTodo.class);

        if (prepareTodo != null) {
            for (int i = 0; i < prepareTodo.value(); i++) {
                new TodoRequest(RequestSpec.authSpec())
                        .create(generateTodo());
            }
        }
    }
}
