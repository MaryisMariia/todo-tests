package com.todo.storages;

import com.todo.models.Todo;

import java.util.HashMap;

public class TestDataStorage {
    private static TestDataStorage instance;
    private HashMap<Long, Todo> storage;

    private TestDataStorage() {
        storage = new HashMap<>();
    }

    public static TestDataStorage getInstance() {
        if (instance == null) {
            instance = new TestDataStorage();
        }
        return instance;
    }

    public void addData(Todo todo) {
        storage.put(todo.getId(), todo);
    }

    public HashMap<Long, Todo> getStorage() {
        return storage;
    }

    public void clean() {
        storage = new HashMap<>();
    }
}
