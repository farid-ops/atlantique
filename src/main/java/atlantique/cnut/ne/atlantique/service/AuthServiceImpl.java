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
    private static final String RATE_LIMIT_PREFIX = "rate_limit_token:";
    private static final String TOKEN_PREFIX = "token:";
    private final StringRedisTemplate redisTemplate;
    private static final int RATE_LIMIT = 5;
    private static final long RATE_LIMIT_PERIOD_MINUTES = 15;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UtilisateurService utilisateurService, JwtDecoder jwtDecoder, UtilService utilService, JwtUtils jwtUtils, StringRedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.utilisateurService = utilisateurService;
        this.jwtDecoder = jwtDecoder;
        this.utilService = utilService;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, Object> genToken(Oauth2DTO oauth2DTO) {

        String rateLimitKey = RATE_LIMIT_PREFIX.concat(oauth2DTO.getMsisdn());

        if (!this.isRateLimited(rateLimitKey)) return this.utilService.response(StatusCode.HTTP_RATE_LIMIT_EXCEEDED.getStatus_code(), false, StatusCode.HTTP_RATE_LIMIT_EXCEEDED.getStatus_message(), null);

        Map<String, Object> data = new HashMap<>();

        if(oauth2DTO.getGrantType().equals("password")){

            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(oauth2DTO.getMsisdn(),oauth2DTO.getPassword()));

            data=this.jwtUtils.generateJwtToken(authentication.getName(),authentication.getAuthorities(),oauth2DTO.isWithRefreshToken());
            data.put("type", "bearer_token");
            data.put("expires_in", "1H");

            return this.utilService.response(StatusCode.HTTP_TOKEN_GENERATED.getStatus_code(), true, StatusCode.HTTP_TOKEN_GENERATED.getStatus_message(), data);

        } else if(oauth2DTO.getGrantType().equals("refreshToken")){
            String refreshToken=oauth2DTO.getRefreshToken();

            if(refreshToken==null) {
                return this.utilService.response(StatusCode.HTTP_UNAUTHORIZED.getStatus_code(), false, StatusCode.HTTP_UNAUTHORIZED.getStatus_message(), null);
            }

            Jwt decodedJwt = this.jwtDecoder.decode(refreshToken);
            String msisdn=decodedJwt.getSubject();
            Optional<Utilisateur> utilisateur=this.utilisateurService.findByPhone(msisdn);
            Collection<GrantedAuthority> authorities=utilisateur.get().getAuthorites()
                    .stream()
                    .map(role->new SimpleGrantedAuthority(role.getNom()))
                    .collect(Collectors.toList());
            data=this.jwtUtils.generateJwtToken(utilisateur.get().getPhone(),authorities,oauth2DTO.isWithRefreshToken());
            data.put("type", "bearer_token");
            data.put("expires_in", "1H");

            return this.utilService.response(StatusCode.HTTP_TOKEN_GENERATED.getStatus_code(), true, StatusCode.HTTP_TOKEN_GENERATED.getStatus_message(), data);
        }
        data.put("grantType", "grantType <<%s>> not supported ".concat(oauth2DTO.getGrantType()));
        return this.utilService.response(StatusCode.HTTP_UNAUTHORIZED.getStatus_code(), false, StatusCode.HTTP_UNAUTHORIZED.getStatus_message(), oauth2DTO.getGrantType());
    }

    @Override
    public Map<String, Object> logOut(String msisdn) {

        String tokenKey= TOKEN_PREFIX.concat(msisdn);
        String rateLimitKey= RATE_LIMIT_PREFIX.concat(msisdn);


        String token = this.redisTemplate.opsForValue().get(tokenKey);
        String rateLimit = this.redisTemplate.opsForValue().get(rateLimitKey);

        if (token != null && rateLimit!=null){
            this.redisTemplate.delete(tokenKey);
            this.redisTemplate.delete(rateLimitKey);
            return this.utilService.response(StatusCode.HTTP_LOGOUT_SUCCESS.getStatus_code(), true, StatusCode.HTTP_LOGOUT_SUCCESS.getStatus_message(), "success logout");
        }

        return this.utilService.response(StatusCode.HTTP_FORBIDDEN.getStatus_code(), false, StatusCode.HTTP_FORBIDDEN.getStatus_message(), null);
    }

    private boolean isRateLimited(String rateLimitKey){

        Long currentCount = this.redisTemplate.opsForValue().increment(rateLimitKey, 1);

        if (currentCount==1) this.redisTemplate.expire(rateLimitKey, RATE_LIMIT_PERIOD_MINUTES, TimeUnit.MINUTES);

        return currentCount <= RATE_LIMIT;
    }

}