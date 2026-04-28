package com.example.mini_ai_bot.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "data")
public class AIModel {

    @Id
    private String id;

    private String name;  // User's name or Session name

    private String title;

    // FIX: Use a dedicated Message class, not the parent AIModel class
    private List<ChatMessage> messages = new ArrayList<>();

    // Inner class to represent the actual text bubbles
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage {
        private String role;    // "user" or "model"
        private String content; // The text content
    }
}