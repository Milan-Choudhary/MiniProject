package com.example.mini_ai_bot.controller;

import com.example.mini_ai_bot.dto.ChatRequest;
import com.example.mini_ai_bot.model.AIModel;
import com.example.mini_ai_bot.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody ChatRequest request, Authentication authentication) {
        // userId is now derived from the authenticated token
        String userId = authentication.getName();
        return chatService.askGemini(request.getSessionId(), userId, request.getTopic(), request.getMessage());
    }

    @GetMapping("/conversations")
    public List<AIModel> getMyConversations(Authentication authentication) {
        return chatService.getConversationsByUserId(authentication.getName());
    }
}