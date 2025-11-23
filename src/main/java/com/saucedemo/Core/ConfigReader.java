package com.saucedemo.Core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    public static Properties loadProperties(String env) {
        Properties properties = new Properties();
        String fileName = String.format("config/%s.properties", env);

        try (InputStream inputStream =
                     ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found: " + fileName);
            }

            properties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration: " + fileName, e);
        }
        return properties;
    }
}