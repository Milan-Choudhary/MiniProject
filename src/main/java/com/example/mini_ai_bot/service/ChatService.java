package com.example.mini_ai_bot.service;

import com.example.mini_ai_bot.model.AIModel;
import com.example.mini_ai_bot.repository.chatbotrepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final chatbotrepo repository;
    private final ConversationCacheService cacheService;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    public ChatService(chatbotrepo repository, ConversationCacheService cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
        this.restTemplate = new RestTemplate();
    }

    public String askGemini(String sessionId, String userMessage) {
        // 1. Manage Session — check Redis cache first, then fall back to MongoDB
        AIModel session = cacheService.getSession(sessionId);
        if (session == null) {
            session = repository.findById(sessionId).orElse(new AIModel());
        }
        if (session.getId() == null) {
            session.setId(sessionId);
            // Optionally set a title for the sidebar based on the first message
            session.setTitle(userMessage.length() > 20 ? userMessage.substring(0, 20) + "..." : userMessage);
        }

        // 2. Save User Message to History
        session.getMessages().add(new AIModel.ChatMessage("user", userMessage));

        // 3. Prepare the payload for Gemini API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", userMessage)
                ))
        ));

        try {
            // 4. Make the API Call
            String url = GEMINI_URL + apiKey;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            // 5. Extract and format the response
            String botResponse = extractTextFromResponse(response);

            // 4. Save History to MongoDB, then update Redis cache
            session.getMessages().add(new AIModel.ChatMessage("model", botResponse));
            repository.save(session);
            cacheService.cacheSession(session);

            return botResponse;

        } catch (Exception e) {
            // This is where the advanced error handling goes
            String errorMessage = e.getMessage();
            System.err.println("Gemini API Error: " + errorMessage);

            if (errorMessage != null && errorMessage.contains("503")) {
                return "⚠️ **High Demand:** The AI is currently experiencing high traffic. Please wait a few moments and try again.";
            } else if (errorMessage != null && errorMessage.contains("401")) {
                return "⚠️ **Authentication Error:** Please check if your Gemini API key is valid.";
            } else if (errorMessage != null && errorMessage.contains("404")) {
                return "⚠️ **Not Found:** The model endpoint is incorrect. Please check the model URL.";
            }

            return "Sorry, I encountered an error while processing your request. Please try again.";
        }
    }

    public List<AIModel> getRecentSessions() {
        return cacheService.getRecentSessions();
    }

    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            if (response == null || !response.containsKey("candidates")) {
                return "AI returned an empty response.";
            }

            List<?> candidates = (List<?>) response.get("candidates");
            if (candidates.isEmpty()) return "No candidates found.";

            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);

            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "AI responded, but the text could not be extracted.";
        }
    }
}