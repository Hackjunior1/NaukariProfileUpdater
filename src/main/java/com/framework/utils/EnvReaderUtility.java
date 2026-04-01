package com.framework.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvReaderUtility {
    private static final Logger logger = LogManager.getLogger(EnvReaderUtility.class);

    // Initialize Dotenv to load the .env file from the project root
    private static final ThreadLocal<Dotenv> dotenv = ThreadLocal.withInitial(() -> Dotenv.configure()
            .ignoreIfMissing() // Prevents crashes if running in CI/CD where .env might not exist (injected via pipeline vars instead)
            .load());

    /**
     * Retrieves the secure credential from the .env file.
     *
     * @param key The key defined in the .env file (e.g., "ADMIN_PASSWORD")
     * @return The secure String value, or null if not found
     */
    public static String getCredential(String key) {
        String value = dotenv.get().get(key);

//      Fallback: Check System Environment Variables (GitLab CI)
        if (value == null) {
            value = System.getenv(key);
        }

        if (value == null) {
            logger.warn("Critical Warning: Credential key '{}' was not found in the .env file.", key);
        }
        return value;
    }
}