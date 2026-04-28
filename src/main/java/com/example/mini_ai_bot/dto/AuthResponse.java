package com.example.mini_ai_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String token;       // JWT Bearer token
    private String userId;      // MongoDB _id of the user
    private String name;        // Display name
    private String email;
    private String message;     // e.g. "Login successful"
}