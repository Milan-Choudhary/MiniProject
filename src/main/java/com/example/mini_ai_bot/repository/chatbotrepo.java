package com.example.mini_ai_bot.repository;

import com.example.mini_ai_bot.model.AIModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface chatbotrepo extends MongoRepository<AIModel, String> {

    // Custom query to find all chat sessions for a specific person/name
    List<AIModel> findByName(String name);

    // Custom query to find a session by title (useful for a sidebar list)
    List<AIModel> findByTitleContainingIgnoreCase(String title);

    // Find all sessions for a user
    List<AIModel> findByUserId(String userId);

    // Find all sessions for a user by topic
    List<AIModel> findByUserIdAndTopic(String userId, String topic);
}