package com.example.CodeIntel_AI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    // Fast and Low-Traffic Gemini models list (Fallback order)
    private final String[] lowTrafficModels = {
            "gemini-1.5-flash",
            "gemini-1.5-flash-8b",
            "gemini-1.5-pro",
            "gemini-1.0-pro"
    };

    public String getAiResponse(String userPrompt) {
        for (String model : lowTrafficModels) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

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
                                    return "### [Model Used: " + model + "]\n\n" + aiText;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Model [" + model + "] failed or busy: " + e.getMessage() + ". Trying next model...");
            }
        }
        return "All Gemini models are currently busy. Please try again in a few seconds.";
    }
}