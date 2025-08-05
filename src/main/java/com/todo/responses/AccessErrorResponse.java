package com.todo.responses;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class AccessErrorResponse {
    public static ResponseSpecification userIsUnauthorized() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_UNAUTHORIZED);
        responseSpecBuilder.expectBody("message", Matchers.containsString("User is unauthorized"));
        return responseSpecBuilder.build();
    }
}
