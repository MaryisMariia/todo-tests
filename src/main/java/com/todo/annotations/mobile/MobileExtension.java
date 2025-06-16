package com.todo.annotations.mobile;

import com.todo.config.ConfigManager;
import com.todo.requests.TodoRequest;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MobileExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        var testMethod = context.getRequiredTestMethod();

        var mobile = testMethod.getAnnotation(Mobile.class);

        if (mobile != null) {
            TodoRequest.setTodoEndpoint(ConfigManager.getProperties().getProperty("TODO_ENDPOINT_MOBILE"));
        }
    }
}
