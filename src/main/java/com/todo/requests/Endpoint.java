package com.todo.requests;

import com.todo.conf.ConfigManager;

public class Endpoint {

    private final String endpoint;


    public Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String build() {
        return ConfigManager.getProperty("baseUrl") + endpoint;
    }
}
