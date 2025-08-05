package com.todo.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;

    private final Properties properties = new Properties();

    private ConfigManager() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("config.properties file not found in resources");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }

    private static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public static String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }

}
