package com.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.pojo.GeminiRequest;
import com.framework.pojo.GeminiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * GeminiUtils — Wrapper around the Google Gemini REST API.
 *
 * SETUP (one-time, per machine):
 *   1. Copy  .env.example  →  .env  in the project root.
 *   2. Paste your Gemini API key (https://aistudio.google.com/apikeys) as:
 *         GEMINI_API_KEY=your_key_here
 *   3. In IntelliJ, open Run/Debug Configurations for any test runner
 *      and add the same value under "Environment variables" if you prefer
 *      NOT to use the .env file.
 *
 * No new Maven dependencies are required — uses Java 21 built-in HttpClient
 * with the Jackson and Lombok libraries already in this project.
 */
public class GeminiUtils {

    private static final Logger logger = LogManager.getLogger(GeminiUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    // Values are loaded once from config.properties
    private static final String MODEL   = ConfigReader.getProperty("gemini.model");
    private static final String API_URL = ConfigReader.getProperty("gemini.api.url");

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Sends a free-form prompt to Gemini and returns the plain-text reply.
     *
     * @param prompt Any text prompt
     * @return The model's text response
     */
    public static String askGemini(String prompt) {
        return callApi(prompt, 0.7, 2048);
    }

    /**
     * Generates realistic test data for the described field.
     * Returns ONLY the value — no explanation — so it can be used directly
     * in WebElement.sendKeys() calls.
     *
     * Example:
     *   String jobTitle = GeminiUtils.generateTestData("A senior Java developer job title");
     *
     * @param fieldDescription Natural-language description of the data needed
     * @return A single realistic value
     */
    public static String generateTestData(String fieldDescription) {
        String prompt = "Generate a single realistic value for the following field used in UI "
                + "automation testing. Return ONLY the value, no labels or explanation:\n"
                + fieldDescription;
        return callApi(prompt, 1.0, 256);
    }

    /**
     * Analyses a Selenium/Cucumber test failure and suggests a fix.
     *
     * Example:
     *   String fix = GeminiUtils.analyzeTestFailure(
     *       driver.findElement(...) exception message,
     *       "Step: User clicks the Save button");
     *
     * @param errorMessage   The exception or failure message from the test output
     * @param stepDescription The Cucumber step or action that caused the failure
     * @return Diagnosis and actionable fix suggestion
     */
    public static String analyzeTestFailure(String errorMessage, String stepDescription) {
        String prompt = String.format(
                "I have a Selenium + Cucumber test automation failure. Analyse it and suggest a fix.\n\n"
                + "Failed step: %s\n\n"
                + "Error message:\n%s\n\n"
                + "Respond with: (1) Root cause, (2) Recommended fix, (3) Code snippet if applicable.",
                stepDescription, errorMessage);
        return callApi(prompt, 0.3, 1024);
    }

    /**
     * Suggests a CSS selector or XPath for a described web element.
     *
     * Example:
     *   String locator = GeminiUtils.suggestLocator(
     *       "The 'Update Profile' button in the header of naukri.com after login");
     *
     * @param elementDescription Natural-language description of the element and its page context
     * @return Suggested locator with a brief explanation
     */
    public static String suggestLocator(String elementDescription) {
        String prompt = "Suggest the most reliable CSS selector or XPath for the following web element. "
                + "Prefer CSS selectors. Return only the locator string followed by one line of explanation:\n"
                + elementDescription;
        return callApi(prompt, 0.2, 512);
    }

    /**
     * Generates Cucumber Gherkin steps for the described scenario.
     *
     * Example:
     *   String steps = GeminiUtils.generateCucumberSteps(
     *       "User updates their Naukri headline and saves the profile");
     *
     * @param scenarioDescription Plain-English description of what should happen
     * @return Gherkin Given/When/Then steps ready to paste into a .feature file
     */
    public static String generateCucumberSteps(String scenarioDescription) {
        String prompt = "Write Cucumber Gherkin steps (Given / When / Then) for the following "
                + "scenario in a Naukri.com profile-update automation test. "
                + "Return only the steps, no extra text:\n"
                + scenarioDescription;
        return callApi(prompt, 0.5, 512);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private static String callApi(String prompt, double temperature, int maxTokens) {
        String apiKey = EnvReaderUtility.getCredential("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY is not set. Copy .env.example to .env and add your key.");
        }
        if (MODEL == null || API_URL == null) {
            throw new IllegalStateException(
                    "gemini.model or gemini.api.url is missing from config.properties.");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(buildRequest(prompt, temperature, maxTokens));
            String url = API_URL + "/" + MODEL + ":generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            logger.debug("Calling Gemini API — model: {}", MODEL);
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Gemini API error [HTTP {}]: {}", response.statusCode(), response.body());
                throw new RuntimeException(
                        "Gemini API returned HTTP " + response.statusCode() + ": " + response.body());
            }

            GeminiResponse geminiResponse = objectMapper.readValue(response.body(), GeminiResponse.class);

            if (geminiResponse.getError() != null) {
                throw new RuntimeException(
                        "Gemini API error: " + geminiResponse.getError().getMessage());
            }

            return extractText(geminiResponse);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gemini API call was interrupted", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error calling Gemini API", e);
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    private static GeminiRequest buildRequest(String prompt, double temperature, int maxTokens) {
        GeminiRequest.Part part             = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content       = new GeminiRequest.Content(List.of(part));
        GeminiRequest.GenerationConfig cfg  = new GeminiRequest.GenerationConfig(temperature, maxTokens);
        return new GeminiRequest(List.of(content), cfg);
    }

    private static String extractText(GeminiResponse response) {
        if (response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new RuntimeException("Gemini returned no candidates.");
        }
        GeminiResponse.Candidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null
                || candidate.getContent().getParts() == null
                || candidate.getContent().getParts().isEmpty()) {
            throw new RuntimeException("Gemini candidate contains no content parts.");
        }
        return candidate.getContent().getParts().get(0).getText().strip();
    }
}
