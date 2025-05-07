package dev.clinic.mainservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public RedisCacheManager cacheManager(ObjectMapper redisObjectMapper) {
        RedisCacheConfiguration cacheConfig = myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper)
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(cacheConfig)
                .withCacheConfiguration("appointments", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("appointmentsList", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))

                .withCacheConfiguration("users", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("usersList", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))

                .withCacheConfiguration("branches", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("branchesList", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))

                .withCacheConfiguration("schedules", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("schedulesList", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))

                .withCacheConfiguration("pets", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("petsList", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))

                .withCacheConfiguration("medicalRecords", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("medicalRecordsList", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .build();
    }

    private RedisCacheConfiguration myDefaultCacheConfig(Duration duration, ObjectMapper objectMapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(duration)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
