package com.example.mini_ai_bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat_sessions") // Better collection name for clarity
public class AIModel {

    @Id
    private String id;

    private String userId;

    private String topic;

    private String name;  // User's name or Session name

    private String name;  // Can be used later if you add user accounts
    private String title; // Can be used for the sidebar history

    private List<ChatMessage> messages = new ArrayList<>();

    // Inner class representing individual chat bubbles
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage {
        private String role;    // "user" or "model"
        private String content; // The actual message text
    }
}