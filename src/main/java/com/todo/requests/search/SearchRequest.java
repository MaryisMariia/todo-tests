package com.todo.requests.search;

import com.todo.requests.Request;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static com.todo.constants.Endpoints.TODO_ENDPOINT;
import static io.restassured.RestAssured.given;

public class SearchRequest extends Request implements SearchInterface {

    public SearchRequest(RequestSpecification reqSpec) {
        super(reqSpec);
    }

    @Override
    public Response readAll(Object offset, Object limit) {
        return given()
                .spec(reqSpec)
                .param("offset", offset)
                .param("limit", limit)
                .when()
                .get(TODO_ENDPOINT);
    }

    @Override
    public Response readAll() {
        return given()
                .spec(reqSpec)
                .when()
                .get(TODO_ENDPOINT);
    }
}
