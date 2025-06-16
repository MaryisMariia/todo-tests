package com.todo.specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static com.todo.constants.UserCreds.PASSWORD;
import static com.todo.constants.UserCreds.USERNAME;

public class RequestSpec {
    private RequestSpecBuilder requestSpecBuilder;

    private final ContentType contentType;

    public RequestSpec(ContentType contentType) {
        this.contentType = contentType;
    }

    private RequestSpecBuilder baseSpecBuilder() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addFilters(List.of(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()));
        requestSpecBuilder.setContentType(contentType);
        requestSpecBuilder.setAccept(ContentType.ANY);
        return requestSpecBuilder;
    }

    public RequestSpecification unauthSpec() {
        return baseSpecBuilder().build();
    }

    public RequestSpecification authSpec() {
        return authSpec(USERNAME, PASSWORD);
    }

    public RequestSpecification authSpec(String username, String password) {
        PreemptiveBasicAuthScheme basicAuthScheme = new PreemptiveBasicAuthScheme();
        basicAuthScheme.setUserName(username);
        basicAuthScheme.setPassword(password);
        return baseSpecBuilder().setAuth(basicAuthScheme).build();
    }
}
