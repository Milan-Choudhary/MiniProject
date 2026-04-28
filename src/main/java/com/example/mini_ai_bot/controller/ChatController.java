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

    // Constructor Injection is preferred over @Autowired on fields
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String message = payload.get("message");
        String userId = payload.get("userId");
        String topic = payload.get("topic");

        if (sessionId == null || message == null) {
            return "Error: Please provide both sessionId and message in the request body.";
        }

        return chatService.askGemini(sessionId, userId, topic, message);
    }

    @GetMapping("/conversations/user/{userId}")
    public List<AIModel> getUserConversations(@PathVariable String userId) {
        return chatService.getConversationsByUserId(userId);
    }

    @GetMapping("/conversations/user/{userId}/topic/{topic}")
    public List<AIModel> getUserConversationsByTopic(@PathVariable String userId, @PathVariable String topic) {
        return chatService.getConversationsByUserIdAndTopic(userId, topic);
    }
}