package com.todo.specs;

import com.todo.conf.ConfigManager;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpec {

    public static RequestSpecBuilder baseSpec(){
       return new RequestSpecBuilder()
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);
    }

    public static RequestSpecification unauthSpec(){
        return baseSpec().build();
    }

    public static RequestSpecification authSpec(){
        PreemptiveBasicAuthScheme basicAuthScheme = new PreemptiveBasicAuthScheme();
        basicAuthScheme.setUserName(ConfigManager.getProperty("login"));
        basicAuthScheme.setPassword(ConfigManager.getProperty("password"));
        return baseSpec().setAuth(basicAuthScheme).build();
    }
}
