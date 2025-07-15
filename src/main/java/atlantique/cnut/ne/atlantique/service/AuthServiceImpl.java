package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.Oauth2DTO;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.security.JwtUtils;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final UtilisateurService utilisateurService;
    private final JwtDecoder jwtDecoder;
    private final UtilService utilService;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String RATE_LIMIT_PREFIX = "rate_limit_token:";
    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final int RATE_LIMIT = 5;
    private static final long RATE_LIMIT_PERIOD_MINUTES = 15;

    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 3600;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 7 * 24 * 3600;


    public AuthServiceImpl(AuthenticationManager authenticationManager, UtilisateurService utilisateurService, JwtDecoder jwtDecoder, UtilService utilService, JwtUtils jwtUtils, StringRedisTemplate redisTemplate, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.utilisateurService = utilisateurService;
        this.jwtDecoder = jwtDecoder;
        this.utilService = utilService;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public Map<String, Object> genToken(Oauth2DTO oauth2DTO) {

        String msisdn = oauth2DTO.getMsisdn();
        String rateLimitKey = RATE_LIMIT_PREFIX.concat(msisdn);

        if (!this.isRateLimited(rateLimitKey)) {
            return this.utilService.response(
                    StatusCode.HTTP_RATE_LIMIT_EXCEEDED.getStatus_code(),
                    false,
                    StatusCode.HTTP_RATE_LIMIT_EXCEEDED.getStatus_message(),
                    null
            );
        }

        Instant now = Instant.now();
        String accessToken = null;
        String refreshToken = null;
        Collection<? extends GrantedAuthority> authorities;
        String subject;

        if ("password".equalsIgnoreCase(oauth2DTO.getGrantType())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(msisdn, oauth2DTO.getPassword())
            );
            subject = authentication.getName();
            authorities = authentication.getAuthorities();

            accessToken = this.jwtUtils.generateToken(subject, authorities, ACCESS_TOKEN_EXPIRATION_SECONDS, "access");

            if (oauth2DTO.isWithRefreshToken()) {
                refreshToken = this.jwtUtils.generateToken(subject, authorities, REFRESH_TOKEN_EXPIRATION_SECONDS, "refresh");
                redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + subject, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));
            }

        } else if ("refreshToken".equalsIgnoreCase(oauth2DTO.getGrantType())) {
            String providedRefreshToken = oauth2DTO.getRefreshToken();
            if (providedRefreshToken == null || providedRefreshToken.isEmpty()) {
                return this.utilService.response(
                        StatusCode.HTTP_UNAUTHORIZED.getStatus_code(),
                        false,
                        StatusCode.HTTP_UNAUTHORIZED.getStatus_message(),
                        "Jeton de rafraîchissement manquant."
                );
            }

            try {
                Jwt decodedRefreshToken = jwtDecoder.decode(providedRefreshToken);
                subject = decodedRefreshToken.getSubject();
                String tokenType = decodedRefreshToken.getClaimAsString("type");

                if (!"refresh".equals(tokenType)) {
                    return this.utilService.response(
                            StatusCode.HTTP_UNAUTHORIZED.getStatus_code(),
                            false,
                            StatusCode.HTTP_UNAUTHORIZED.getStatus_message(),
                            "Type de jeton de rafraîchissement invalide."
                    );
                }

                String storedRefreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + subject);

                if (storedRefreshToken == null || !storedRefreshToken.equals(providedRefreshToken)) {
                    return this.utilService.response(
                            StatusCode.HTTP_UNAUTHORIZED.getStatus_code(),
                            false,
                            StatusCode.HTTP_UNAUTHORIZED.getStatus_message(),
                            "Jeton de rafraîchissement non valide ou expiré (Redis)."
                    );
                }

                Optional<Utilisateur> utilisateurOptional = this.utilisateurService.findByPhone(subject);
                if (utilisateurOptional.isEmpty()) {
                    return this.utilService.response(
                            StatusCode.HTTP_UNAUTHORIZED.getStatus_code(),
                            false,
                            StatusCode.HTTP_UNAUTHORIZED.getStatus_message(),
                            "Utilisateur non trouvé pour le jeton de rafraîchissement."
                    );
                }
                authorities = utilisateurOptional.get().getAuthorites().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getNom().toUpperCase()))
                        .collect(Collectors.toList());

                accessToken = jwtUtils.generateToken(subject, authorities, ACCESS_TOKEN_EXPIRATION_SECONDS, "access");

                if (oauth2DTO.isWithRefreshToken()) {
                    redisTemplate.delete(REFRESH_TOKEN_PREFIX + subject);
                    refreshToken = jwtUtils.generateToken(subject, authorities, REFRESH_TOKEN_EXPIRATION_SECONDS, "refresh");
                    redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + subject, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));
                } else {
                    refreshToken = providedRefreshToken;
                }


            } catch (Exception e) {
                return this.utilService.response(
                        StatusCode.HTTP_UNAUTHORIZED.getStatus_code(),
                        false,
                        StatusCode.HTTP_UNAUTHORIZED.getStatus_message(),
                        "Jeton de rafraîchissement invalide ou malformé: " + e.getMessage()
                );
            }

        } else {
            return this.utilService.response(
                    StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                    false,
                    StatusCode.HTTP_BAD_REQUEST.getStatus_message(),
                    "Type de grant non supporté: " + oauth2DTO.getGrantType()
            );
        }

        Map<String, Object> data = new HashMap<>();
        data.put("access_token", accessToken);
        if (refreshToken != null) {
            data.put("refresh_token", refreshToken);
        }
        data.put("token_type", "Bearer");
        data.put("expires_in", ACCESS_TOKEN_EXPIRATION_SECONDS);

        return this.utilService.response(
                StatusCode.HTTP_TOKEN_GENERATED.getStatus_code(),
                true,
                StatusCode.HTTP_TOKEN_GENERATED.getStatus_message(),
                data
        );
    }

    @Override
    public Map<String, Object> logOut(String msisdn, String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            this.tokenBlacklistService.blacklist(accessToken);
        }

        Boolean deletedRefreshToken = redisTemplate.delete(REFRESH_TOKEN_PREFIX + msisdn);

        Boolean deletedRateLimit = redisTemplate.delete(RATE_LIMIT_PREFIX + msisdn);

        if (Boolean.TRUE.equals(deletedRefreshToken) || (accessToken != null && tokenBlacklistService.isTokenBlacklisted(accessToken))) {
            return this.utilService.response(
                    StatusCode.HTTP_LOGOUT_SUCCESS.getStatus_code(),
                    true,
                    StatusCode.HTTP_LOGOUT_SUCCESS.getStatus_message(),
                    "Déconnexion réussie pour l'utilisateur: " + msisdn
            );
        } else {
            return this.utilService.response(
                    StatusCode.HTTP_FORBIDDEN.getStatus_code(),
                    false,
                    StatusCode.HTTP_FORBIDDEN.getStatus_message(),
                    "Impossible de déconnecter l'utilisateur. Aucun jeton actif trouvé."
            );
        }
    }

    private boolean isRateLimited(String rateLimitKey){

        Long currentCount = this.redisTemplate.opsForValue().increment(rateLimitKey, 1);

        if (currentCount==1) this.redisTemplate.expire(rateLimitKey, RATE_LIMIT_PERIOD_MINUTES, TimeUnit.MINUTES);

        return currentCount <= RATE_LIMIT;
    }

}