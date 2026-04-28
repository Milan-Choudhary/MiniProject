package com.example.mini_ai_bot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {

    // Client-generated UUID that groups messages into one conversation
    @NotBlank(message = "sessionId is required")
    private String sessionId;

    @NotBlank(message = "message is required")
    private String message;

    // Optional: scope this session to a topic (e.g. "Python", "History")
    private String topic;
}