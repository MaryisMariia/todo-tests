package com.todo.respvalidators;

import io.restassured.response.Response;

public class ValidatedBaseResponse {

    private final Response response;

    public ValidatedBaseResponse(Response response) {
        this.response = response;
    }

    public void assertStatusCode(int status) {
        response.then()
                .assertThat()
                .statusCode(status);
    }

    public <T> T extractResponse(Class<T> clazz) {
        return response.then()
                .extract()
                .as(clazz);
    }

    public String extractErrorResponse() {
        return response.asString();
    }

}
