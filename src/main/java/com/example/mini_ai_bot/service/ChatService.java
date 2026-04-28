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
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    // UPDATE: Changed model from 'gemini-1.5-flash' (retired) to 'gemini-2.5-flash' (active)
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    public ChatService(chatbotrepo repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public String askGemini(String sessionId, String userId, String topic, String userMessage) {
        // 1. Manage Session
        AIModel session = repository.findById(sessionId).orElse(new AIModel());
        if (session.getId() == null) {
            session.setId(sessionId);
        }
        if (session.getUserId() == null && userId != null) {
            session.setUserId(userId);
        }
        if (session.getTopic() == null && topic != null) {
            session.setTopic(topic);
        }

        // 2. Add User Message
        session.getMessages().add(new AIModel.ChatMessage("user", userMessage));

        // 3. Prepare API Call
        String url = GEMINI_URL + apiKey;

        List<Map<String, Object>> contents = new java.util.ArrayList<>();

        // Context Window: Add all previous messages
        for (AIModel.ChatMessage msg : session.getMessages()) {
            // Ensure content is not null to prevent API errors
            String textContent = msg.getContent() != null ? msg.getContent() : "";
            contents.add(Map.of(
                    "role", msg.getRole() != null ? msg.getRole() : "user",
                    "parts", List.of(Map.of("text", textContent))
            ));
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);

        // Proper Gemini API System Instruction mapping
        if (topic != null && !topic.isEmpty()) {
            requestBody.put("system_instruction", Map.of(
                    "parts", List.of(Map.of("text", "You are an expert on the topic of " + topic + ". Please keep the conversation focused on this topic."))
            ));
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            String botResponse = extractTextFromResponse(response);

            // 4. Save History
            session.getMessages().add(new AIModel.ChatMessage("model", botResponse));
            repository.save(session);

            return botResponse;
        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            if (response == null || !response.containsKey("candidates")) {
                return "AI returned an empty response.";
            }

            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);

            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "AI responded, but the data format was unexpected.";
        }
    }

    public List<AIModel> getConversationsByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public List<AIModel> getConversationsByUserIdAndTopic(String userId, String topic) {
        return repository.findByUserIdAndTopic(userId, topic);
    }
}