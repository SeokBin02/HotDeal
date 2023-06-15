package challenge18.hotdeal.common.config.Redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // For Lettuce Client
    @Bean(name = "redisConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    // Redis Cache 적용을 위한 RedisCacheManager 설정
    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(@Qualifier("redisConnectionFactory")  RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));


        Map<String, RedisCacheConfiguration> cacheConfigMap = new HashMap<>();
        cacheConfigMap.put(RedisCacheKey.USER, redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        cacheConfigMap.put(RedisCacheKey.PRODUCT, redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        cacheConfigMap.put(RedisCacheKey.LIMITED, redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        cacheConfigMap.put(RedisCacheKey.PURCHASE, redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        cacheConfigMap.put(RedisCacheKey.POPULAR_ITEM, redisCacheConfiguration.entryTtl(Duration.ofHours(25)));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigMap)
                .build();
    }

    // For Redisson Client
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        log.info("redis://"+host+":"+port);
        config.useSingleServer().setAddress("redis://"+host+":"+port);
        return Redisson.create(config);
    }
}
