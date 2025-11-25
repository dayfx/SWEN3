package com.fhtechnikum.paperlessservices.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GenAIService {

    private static final Logger log = LoggerFactory.getLogger(GenAIService.class);

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.api-url}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateSummary(String documentText) {
        if (documentText == null || documentText.trim().isEmpty()) {
            log.warn("Cannot generate summary for empty text");
            return null;
        }

        log.info("=== GenAI - Generating Summary ===");
        log.info("Text length: {} characters", documentText.length());

        try {
            String url = String.format("%s/%s:generateContent", apiUrl, model);
            log.info("API URL: {}", url);

            String escapedText = objectMapper.writeValueAsString(
                "Please provide a concise summary (3-5 sentences) of the following document:\n\n" + documentText
            );

            String jsonBody = String.format("""
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": %s
                        }
                      ]
                    }
                  ]
                }
                """, escapedText);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            log.info("Sending request to Gemini API...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Gemini API response: HTTP {}", response.statusCode());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String summary = root.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
                log.info("Summary generated: {} characters", summary.length());
                return summary;
            } else {
                log.error("Gemini API error: HTTP {}", response.statusCode());
                log.error("Response body: {}", response.body());
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to generate summary: {}", e.getMessage(), e);
            return null;
        }
    }
}
