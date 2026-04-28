package com.example.mini_ai_bot.controller;

import com.example.mini_ai_bot.model.AIModel;
import com.example.mini_ai_bot.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String message = payload.get("message");
        //New Lines

        if (sessionId == null || message == null) {
            return "Error: Please provide both sessionId and message in the request body.";
        }

        return chatService.askGemini(sessionId, message);
    }

    @GetMapping("/recent")
    public List<AIModel> getRecentSessions() {
        return chatService.getRecentSessions();
    }
}