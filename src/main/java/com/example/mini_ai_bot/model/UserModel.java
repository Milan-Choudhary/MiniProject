package com.example.mini_ai_bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class UserModel {
    package com.example.mini_ai_bot.model;

    import lombok.*;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Document(collection = "users")
    public class User {

        @Id
        private String id;

        private String username;
        private String password;
    }
}
