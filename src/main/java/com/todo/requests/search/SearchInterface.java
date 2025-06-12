package com.todo.requests.search;

import io.restassured.response.Response;

public interface SearchInterface {

   Response readAll(Object offset, Object limit);

   Response readAll();

}
