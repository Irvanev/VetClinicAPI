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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public ObjectMapper redisObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisHost, redisPort)
        );
    }

    @Bean
    public RedisCacheManager cacheManager(ObjectMapper redisObjectMapper) {
        RedisCacheConfiguration defaultConfig =
                myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper)
                        .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("branches", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("users", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("schedules", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("medicalRecords", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .withCacheConfiguration("pets", myDefaultCacheConfig(Duration.ofMinutes(10), redisObjectMapper))
                .build();
    }

    private RedisCacheConfiguration myDefaultCacheConfig(Duration ttl, ObjectMapper redisObjectMapper) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }
}
