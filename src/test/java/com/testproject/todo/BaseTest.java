package com.testproject.todo;

import com.todo.conf.ConfigManager;
import com.todo.requests.TodoRequest;
import com.todo.requests.TodoRequester;
import com.todo.specs.RequestSpec;
import com.todo.storages.TestDataStorage;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected TodoRequester todoRequester;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = ConfigManager.getProperty("baseUrl");
        RestAssured.port = Integer.parseInt(ConfigManager.getProperty("port"));
    }

    @BeforeEach
    public void setupTest() {
        todoRequester = new TodoRequester(RequestSpec.authSpec());
        softly = new SoftAssertions();
    }

    @AfterEach
    public void clean() {
        TestDataStorage.getInstance().getStorage()
                .forEach((k, v) ->
                        new TodoRequest(RequestSpec.authSpec())
                                .delete(k));
        TestDataStorage.getInstance().clean();
        softly.assertAll();
    }

}
