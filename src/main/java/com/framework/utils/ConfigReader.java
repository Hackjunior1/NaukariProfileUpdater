package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    private static final Properties properties;

    // Static block to load the file into memory once
    static {
        try {
            FileInputStream inputStream = new FileInputStream("src/test/resources/config.properties");
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            logger.error("Configuration file not found at src/test/resources/config.properties", e);
            throw new RuntimeException("Failed to load config.properties file.", e);
        }
    }

    /**
     * Retrieves the value of a property based on the provided key.
     *
     * @param key The key defined in config.properties
     * @return The String value of the property, or null if not found
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Warning: Property key '{}' was not found in config.properties", key);
        }
        return value;
    }
}