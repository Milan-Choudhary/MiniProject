package com.example.mini_ai_bot.service;

import com.example.mini_ai_bot.model.AIModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ConversationCacheService {

    private static final Logger log = LoggerFactory.getLogger(ConversationCacheService.class);
    private static final String RECENT_SESSIONS_KEY = "chat:recent:sessions";
    private static final String SESSION_KEY_PREFIX = "chat:session:";

    @Value("${chat.cache.session-ttl-hours:24}")
    private long sessionTtlHours;

    @Value("${chat.cache.max-recent-sessions:5}")
    private int maxRecentSessions;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ConversationCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns a cached AIModel session, or null on a cache miss or Redis being unavailable.
     */
    public AIModel getSession(String sessionId) {
        try {
            String json = redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + sessionId);
            if (json == null) {
                log.debug("Cache miss for session: {}", sessionId);
                return null;
            }
            log.debug("Cache hit for session: {}", sessionId);
            return objectMapper.readValue(json, AIModel.class);
        } catch (Exception e) {
            log.warn("Redis getSession failed for {}: {}", sessionId, e.getMessage());
            return null;
        }
    }

    /**
     * Writes (or refreshes) a session in Redis and bumps it to the front
     * of the recent-sessions list, keeping at most {@code maxRecentSessions} entries.
     * Redis being unavailable is non-fatal; MongoDB is the source of truth.
     */
    public void cacheSession(AIModel session) {
        try {
            String json = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(
                    SESSION_KEY_PREFIX + session.getId(),
                    json,
                    sessionTtlHours,
                    TimeUnit.HOURS
            );

            // Keep the recent list de-duplicated and capped
            redisTemplate.opsForList().remove(RECENT_SESSIONS_KEY, 0, session.getId());
            redisTemplate.opsForList().leftPush(RECENT_SESSIONS_KEY, session.getId());
            redisTemplate.opsForList().trim(RECENT_SESSIONS_KEY, 0, maxRecentSessions - 1);
            log.info("Cached session {} in Redis", session.getId());
        } catch (Exception e) {
            log.warn("Redis cacheSession failed for {}: {}", session.getId(), e.getMessage());
        }
    }

    /**
     * Returns up to {@code maxRecentSessions} fully-populated sessions from Redis.
     * Returns an empty list if Redis is unavailable.
     */
    public List<AIModel> getRecentSessions() {
        try {
            List<String> ids = redisTemplate.opsForList().range(RECENT_SESSIONS_KEY, 0, maxRecentSessions - 1);
            if (ids == null) return List.of();
            return ids.stream()
                    .map(this::getSession)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Redis getRecentSessions failed: {}", e.getMessage());
            return List.of();
        }
    }
}
