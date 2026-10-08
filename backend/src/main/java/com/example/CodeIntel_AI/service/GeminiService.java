package com.example.CodeIntel_AI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public String getAiResponse(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "API Key is missing in server environment variables.";
        }

        String cleanKey = apiKey.trim();

        // Step 1: Fetch all supported models for this specific API key directly from Google
        List<String> availableModels = fetchAvailableModels(cleanKey);

        if (availableModels.isEmpty()) {
            return "Failed to retrieve model list from Google AI API. Key might be invalid or restricted.";
        }

        // Return the active models list to inspect directly
        System.out.println("Available Models for this Key: " + availableModels);

        // Step 2: Try calling generateContent for each model in the returned list
        for (String modelName : availableModels) {
            // Filter only for generateContent supported models
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/" + modelName + ":generateContent?key=" + cleanKey;

                Map<String, Object> requestBody = Map.of(
                        "contents", List.of(
                                Map.of("parts", List.of(Map.of("text", userPrompt)))
                        )
                );

                Map<?, ?> response = restClient.post()
                        .uri(url)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

                if (response != null && response.containsKey("candidates")) {
                    List<?> candidates = (List<?>) response.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                        if (content != null && content.containsKey("parts")) {
                            List<?> parts = (List<?>) content.get("parts");
                            if (parts != null && !parts.isEmpty()) {
                                Map<?, ?> part = (Map<?, ?>) parts.get(0);
                                String aiText = (String) part.get("text");

                                if (aiText != null && !aiText.isBlank()) {
                                    return aiText;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Execution failed for model [" + modelName + "]: " + e.getMessage());
            }
        }

        return "Supported models found on Google: " + availableModels.toString() + " but prompt generation failed.";
    }

    private List<String> fetchAvailableModels(String cleanKey) {
        try {
            String listModelsUrl = "https://generativelanguage.googleapis.com/v1beta/models?key=" + cleanKey;

            Map<?, ?> response = restClient.get()
                    .uri(listModelsUrl)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("models")) {
                List<Map<String, Object>> modelsList = (List<Map<String, Object>>) response.get("models");
                return modelsList.stream()
                        .map(m -> (String) m.get("name")) // Returns format: "models/gemini-1.5-flash"
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Error fetching models list: " + e.getMessage());
        }
        return List.of();
    }
}