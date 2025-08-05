package com.todo.requests;

import io.restassured.specification.RequestSpecification;
import lombok.Getter;

@Getter
public class TodoRequester {
    private final TodoRequest request;
    private final ValidatedTodoRequest validatedRequest;

    public TodoRequester(RequestSpecification requestSpecification) {
        this.request = new TodoRequest(requestSpecification);
        this.validatedRequest = new ValidatedTodoRequest(requestSpecification);
    }

}
