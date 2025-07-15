package atlantique.cnut.ne.atlantique.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String TOKEN_BLACKLIST = "atlantique.token.blacklist";
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtDecoder jwtDecoder;
    private final RedisTemplate<Object, Object> redisTemplate;

    public TokenBlacklistServiceImpl(StringRedisTemplate stringRedisTemplate, JwtDecoder jwtDecoder, RedisTemplate<Object, Object> redisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtDecoder = jwtDecoder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklist(String token) {
        try{
            Jwt decodedJwt = jwtDecoder.decode(token);
            Instant expiresAt = decodedJwt.getExpiresAt();

            if (expiresAt != null){
                Duration ttl = Duration.between(Instant.now(), expiresAt);
                if (!ttl.isNegative() && !ttl.isZero()){
                    this.redisTemplate.opsForValue().set(TOKEN_BLACKLIST + token, "blacklisted", ttl);
                }
            }

        } catch (Exception e) {
            this.redisTemplate.opsForValue().set(TOKEN_BLACKLIST + token, "blacklisted_error", Duration.ofHours(1));
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        Boolean isBlacklisted = this.redisTemplate.hasKey(TOKEN_BLACKLIST + token);
        return Boolean.TRUE.equals(isBlacklisted);
    }
}
