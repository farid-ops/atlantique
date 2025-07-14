
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("ALL")
public class JwtUtils {
    private final JwtEncoder jwtEncoder;
    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "token:";
    private static final String RATE_LIMIT_PREFIX = "rate_limit_token:";
    private static final int RATE_LIMIT = 1;
    private static final long RATE_LIMIT_PERIOD_HOURS = 1;
    private final UtilService utilService;


    public JwtUtils(JwtEncoder jwtEncoder, StringRedisTemplate redisTemplate, UtilService utilService) {
        this.jwtEncoder = jwtEncoder;
        this.redisTemplate = redisTemplate;
        this.utilService = utilService;
    }

    public Map<String,Object> generateJwtToken(String msisdn, Collection<? extends GrantedAuthority> authorities, boolean withRefreshToken){

        Map<String,Object > idToken=new HashMap<>();

        Instant instant=Instant.now();

        String scope=authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(instant)
                .expiresAt(instant.plus(withRefreshToken ? 1 : 1, ChronoUnit.HOURS))
                .subject(msisdn)
                .claim("scope",scope)
                .build();

        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();

        this.saveToken(msisdn, accessToken);

        idToken.put("access_token",accessToken);
        if(withRefreshToken){
            JwtClaimsSet jwtRefreshTokenClaimsSet=JwtClaimsSet.builder()
                    .issuer("auth-service")
                    .issuedAt(instant)
                    .expiresAt(instant.plus(5, ChronoUnit.HOURS))
                    .subject(msisdn)
                    .build();
            String refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwtRefreshTokenClaimsSet)).getTokenValue();

            this.saveToken(msisdn, refreshToken);

            idToken.put("refresh_token",refreshToken);
        }
        return idToken;
    }

    private void saveToken(String msisdn, String token){
        String tokenKey = TOKEN_PREFIX.concat(msisdn);
        this.redisTemplate.opsForValue().set(tokenKey, token, Duration.ofHours(1));
    }

}