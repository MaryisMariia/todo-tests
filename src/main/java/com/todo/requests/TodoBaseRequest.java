package com.todo.requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static com.todo.constants.Endpoints.TODO_ENDPOINT;
import static io.restassured.RestAssured.given;

public class TodoBaseRequest extends Request implements CrudInterface {

    public TodoBaseRequest(RequestSpecification reqSpec) {
        super(reqSpec);
    }

    @Override
    public Response create(Object entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .when()
                .post(TODO_ENDPOINT);
    }

    @Override
    public Response update(long id, Object entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .when()
                .put(TODO_ENDPOINT + "/" + id);
    }

    @Override
    public Response delete(long id) {
        return given()
                .spec(reqSpec)
                .when()
                .delete(TODO_ENDPOINT + "/" + id);
    }
}
