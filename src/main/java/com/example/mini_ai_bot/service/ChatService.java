package com.example.mini_ai_bot.service;

import com.example.mini_ai_bot.model.AIModel;
import com.example.mini_ai_bot.repository.chatbotrepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ChatService {

    private final chatbotrepo repository;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    public ChatService(chatbotrepo repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public String askGemini(String sessionId, String userId, String topic, String userMessage) {
        AIModel session = repository.findById(sessionId).orElse(new AIModel());

        if (session.getId() == null) {
            session.setId(sessionId);
            // Auto-Rename logic
            String title = userMessage.length() > 30 ? userMessage.substring(0, 30) + "..." : userMessage;
            session.setTitle(title);
        }

        session.setUserId(userId);
        session.setTopic(topic);
        session.getMessages().add(new AIModel.ChatMessage("user", userMessage));

        // ... (API call and response extraction logic remains the same)

        session.getMessages().add(new AIModel.ChatMessage("model", botResponse));
        repository.save(session);
        return botResponse;
    }

    public List<AIModel> getConversationsByUserId(String userId) {
        return repository.findByUserId(userId);
    }
}