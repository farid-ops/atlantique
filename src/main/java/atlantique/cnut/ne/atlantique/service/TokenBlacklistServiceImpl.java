package atlantique.cnut.ne.atlantique.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistServiceImpl.class);
    private static final String TOKEN_BLACKLIST = "blacklist:";
    private final JwtDecoder jwtDecoder;
    private final RedisTemplate<Object, Object> redisTemplate;

    public TokenBlacklistServiceImpl(JwtDecoder jwtDecoder, RedisTemplate<Object, Object> redisTemplate) {
        this.jwtDecoder = jwtDecoder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklist(String token) {
        try{
            Jwt decodedJwt = jwtDecoder.decode(token);
            Instant expiresAt = decodedJwt.getExpiresAt();

            if (expiresAt != null) {
                Duration ttl = Duration.between(Instant.now(), expiresAt);
                if (!ttl.isNegative() && !ttl.isZero()) {
                    redisTemplate.opsForValue().set(TOKEN_BLACKLIST + token, "blacklisted", ttl);
                    logger.info("Token blacklisté dans Redis: {} avec TTL: {}s", token, ttl.getSeconds());
                } else {
                    logger.warn("Le jeton est déjà expiré, pas besoin de le blacklister: {}", token);
                }
            } else {
                logger.warn("Le jeton n'a pas de date d'expiration (exp), non blacklisté: {}", token);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la mise en liste noire du jeton {}: {}", token, e.getMessage(), e);
            redisTemplate.opsForValue().set(TOKEN_BLACKLIST + token, "blacklisted_error", Duration.ofHours(1));
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        Boolean isBlacklisted = this.redisTemplate.hasKey(TOKEN_BLACKLIST + token);
        return Boolean.TRUE.equals(isBlacklisted);
    }
}
