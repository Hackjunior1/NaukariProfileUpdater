package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties;

    // Static block to load the file into memory once
    static {
        try {
            String propertyFilePath = "src/test/resources/config.properties";
            FileInputStream inputStream = new FileInputStream(propertyFilePath);
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Configuration file not found at src/test/resources/config.properties");
            e.fillInStackTrace();
            throw new RuntimeException("Failed to load config.properties file.");
        }
    }

    /**
     * Retrieves the value of a property based on the provided key.
     * @param key The key defined in config.properties
     * @return The String value of the property, or null if not found
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.out.println("Warning: Property key '" + key + "' was not found in config.properties");
        }
        return value;
    }
}