package com.todo.requests;

import com.todo.models.Todo;
import com.todo.requests.interfaces.CrudInterface;
import com.todo.requests.interfaces.SearchInterface;
import com.todo.storages.TestDataStorage;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.util.List;

public class ValidatedTodoRequest extends Request implements CrudInterface<Todo>, SearchInterface<Todo> {
    private final TodoRequest todoRequest;

    public ValidatedTodoRequest(RequestSpecification reqSpec, TodoRequest todoRequest) {
        super(reqSpec);
        this.todoRequest = todoRequest;
    }

    @Override
    public String create(Todo entity) {
        var resp = todoRequest.create(entity)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .asString();
        TestDataStorage.getInstance().addData(entity);
        return resp;
    }

    @Override
    public String update(long id, Todo entity) {
        return todoRequest.update(id, entity)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString();
    }

    @Override
    public String delete(long id) {
        return todoRequest.delete(id)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().body()
                .asString();
    }

    @Override
    public List<Todo> readAll(int offset, int limit) {
        Todo[] todos = todoRequest.readAll(offset, limit)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(Todo[].class);
        return List.of(todos);
    }

    public List<Todo> readAll() {
        Todo[] todos = todoRequest.readAll()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(Todo[].class);
        return List.of(todos);
    }
}
