package likelion13.youcandoittoo.auth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRedisRepositoryImpl implements RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRedisRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean existsByKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }
}

