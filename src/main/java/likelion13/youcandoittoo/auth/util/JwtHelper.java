package likelion13.youcandoittoo.auth.util;

import likelion13.youcandoittoo.global.exception.custom.AuthException;
import likelion13.youcandoittoo.global.exception.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JwtHelper {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final long REFRESH_TOKEN_TTL;

    public JwtHelper(
            JwtUtil jwtUtil,
            RedisTemplate<String, String> redisTemplate,
            @Value("{spring.jwt.refresh-ttl}") long REFRESH_TOKEN_TTL
    ) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.REFRESH_TOKEN_TTL = REFRESH_TOKEN_TTL;
    }

    public void saveRefreshToken(String refreshToken) {

        String username = jwtUtil.getUserName(refreshToken);
        String redisKey = "refresh_token:" + username;

        redisTemplate.opsForHash().put(redisKey, "refreshToken", refreshToken);
        redisTemplate.expire(redisKey, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);
    }

    public boolean isExistRefreshToken(String refreshToken) {

        String username = jwtUtil.getUserName(refreshToken);
        String redisKey = "refresh_token:" + username;
        String storedToken = (String) redisTemplate.opsForHash().get(redisKey, "refreshToken");

        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {

        String username = jwtUtil.getUserName(refreshToken);
        String redisKey = "refresh_token:" + username;

        redisTemplate.delete(redisKey);
    }

    // 하단의 메서드들은 access/refresh 모두 사용될 수 있기에 공용으로 추가
    public void validateToken(String token) {

        jwtUtil.validateToken(token);
        String category = jwtUtil.getCategory(token);

        if (category.equals("refresh")) {
            if (!isExistRefreshToken(token) || isBlacklisted(token)) {
                throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
            }
        } else if (category.equals("access")) {
            if (isBlacklisted(token)) {
                throw new AuthException(ErrorCode.INVALID_ACCESS_TOKEN);
            }
        } else {
            throw new AuthException(ErrorCode.INVALID_TOKEN_CATEGORY);
        }
    }

    public void addBlacklistToken(String token) {

        String redisKey = "blacklist:" + token;
        long ttl = jwtUtil.getExpiration(token) - System.currentTimeMillis();

        redisTemplate.opsForValue().set(redisKey, "true", ttl, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {

        String value = redisTemplate.opsForValue().get("blacklist:" + token);
        return value != null;
    }
}
