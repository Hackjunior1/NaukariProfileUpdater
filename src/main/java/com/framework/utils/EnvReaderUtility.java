package com.framework.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvReaderUtility {

    // Initialize Dotenv to load the .env file from the project root
    private static final ThreadLocal<Dotenv> dotenv = ThreadLocal.withInitial(() -> Dotenv.configure()
            .ignoreIfMissing() // Prevents crashes if running in CI/CD where .env might not exist (injected via pipeline vars instead)
            .load());

    /**
     * Retrieves the secure credential from the .env file.
     * @param key The key defined in the .env file (e.g., "ADMIN_PASSWORD")
     * @return The secure String value, or null if not found
     */
    public static String getCredential(String key) {
        String value = dotenv.get().get(key);
        if (value == null) {
            System.err.println("Critical Warning: Credential key '" + key + "' was not found in the .env file.");
        }
        return value;
    }
}