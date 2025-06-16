package com.todo.config;

import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static Properties properties;

    private ConfigManager() {
        properties = new Properties();
        try {
            //load a properties file from class path, inside static method
            properties.load(ConfigManager.class.getClassLoader().getResourceAsStream("config.properties"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getProperties() {
        if (properties == null) {
            new ConfigManager();
        }
        return properties;
    }
}
