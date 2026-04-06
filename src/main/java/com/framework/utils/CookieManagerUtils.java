package com.framework.utils;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.Date;

public class CookieManagerUtils {

    private final String filePath = "src/test/resources/naukri_cookies.data";

    private final String COOKIE_DELIMITER = ":";
    private final WebDriver driver;

    public CookieManagerUtils(WebDriver driver){
        this.driver = driver;
    }
    // 1. Save all current cookies to a file
    public void saveCookies() {
        if (this.driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            System.err.println("Failed to create directories for cookie file: " + parentDir.getAbsolutePath());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Cookie cookie : driver.manage().getCookies()) {
                StringBuilder sb = new StringBuilder();
                sb.append(cookie.getName()).append(COOKIE_DELIMITER)
                  .append(cookie.getValue()).append(COOKIE_DELIMITER)
                  .append(cookie.getDomain()).append(COOKIE_DELIMITER)
                  .append(cookie.getPath()).append(COOKIE_DELIMITER);

                Date expiry = cookie.getExpiry();
                sb.append(expiry != null ? expiry.getTime() : "null").append(COOKIE_DELIMITER)
                  .append(cookie.isSecure());

                writer.write(sb.toString());
                writer.newLine();
            }

            System.out.println("Cookies saved successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            logException("Error while saving cookies to file: " + file.getAbsolutePath(), e);
        } catch (RuntimeException e) {
            logException("Unexpected error while saving cookies.", e);
        }
    }

    // 2. Load cookies from the file into the current driver
    public void loadCookies() {
        if (this.driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Cookie file does not exist: " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cookie cookie = getCookie(line);
                if (cookie != null) {
                    driver.manage().addCookie(cookie);
                }
            }
            System.out.println("Cookies injected into current session from: " + file.getAbsolutePath());
        } catch (IOException e) {
            logException("Error while loading cookies from file: " + file.getAbsolutePath(), e);
        } catch (RuntimeException e) {
            logException("Unexpected error while loading cookies.", e);
        }
    }

    private Cookie getCookie(String line) {
        if (line == null || line.trim().isEmpty()) {
            System.err.println("Encountered empty cookie line, skipping.");
            return null;
        }

        String[] tokens = line.split(COOKIE_DELIMITER, -1);
        int COOKIE_TOKEN_COUNT = 6;
        if (tokens.length < COOKIE_TOKEN_COUNT) {
            System.err.println("Invalid cookie format, expected " + COOKIE_TOKEN_COUNT
                    + " tokens but got " + tokens.length + ": " + line);
            return null;
        }

        String name = tokens[0];
        String value = tokens[1];
        String domain = tokens[2];
        String path = tokens[3];

        Date expiry = null;
        String expiryToken = tokens[4];
        if (!"null".equalsIgnoreCase(expiryToken) && !expiryToken.isEmpty()) {
            try {
                long expiryTime = Long.parseLong(expiryToken);
                expiry = new Date(expiryTime);
            } catch (NumberFormatException e) {
                System.err.println("Invalid expiry value for cookie '" + name + "': " + expiryToken
                        + ", ignoring expiry.");
            }
        }

        boolean isSecure = Boolean.parseBoolean(tokens[5]);

        return new Cookie(name, value, domain, path, expiry, isSecure);
    }

    private static void logException(String message, Throwable throwable) {
        System.err.println(message);
        if (throwable == null) {
            return;
        }
        System.err.println(throwable.getClass().getName() + ": " + throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            System.err.println("\tat " + element);
        }
    }
}


