package atlantique.cnut.ne.atlantique.security;

import atlantique.cnut.ne.atlantique.records.RsaConfigProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final RsaConfigProperties rsaConfigProperties;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(RsaConfigProperties rsaConfigProperties, UserDetailsServiceImpl userDetailsService) {
        this.rsaConfigProperties = rsaConfigProperties;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf->csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->{

                    auth.requestMatchers("/swagger-ui.html","swagger-ui/**","/v3/**", "/api/v1/oauth2/token",
                            "/api/v1/account/otp/**", "/api/v1/account/new").permitAll();

                    auth.requestMatchers(HttpMethod.GET,  "/api/v1/account/list/**", "/api/v1/account/view/**",
                            "/api/v1/account/receiver_type/**", "/api/v1/account/get-by-msisdn/**",
                            "/api/v1/account/get-balance-by/**", "/api/v1/txn/filter-by-date")
                            .hasAuthority("SCOPE_ADMIN");

                    auth.requestMatchers(HttpMethod.POST,  "/api/v1/account/edit/status", "/api/v1/account/services/enadis",
                                    "/api/v1/account/reset-agent-merchant-pin")
                            .hasAuthority("SCOPE_ADMIN");

                    auth.requestMatchers("/api/v1/transferuv/**", "/api/v1/emoney", "/api/v1/fees/**").hasAuthority("SCOPE_ADMIN");

                    auth.requestMatchers(HttpMethod.PATCH,  "/api/v1/account/edit/data").hasAuthority("SCOPE_ADMIN");

                    auth.requestMatchers(HttpMethod.GET,  "/api/v1/emoney/index", "/api/v1/txn/all/**",
                            "/api/v1/txn/type/**", "/api/v1/account/receiver_type/**",
                            "/api/v1/file/list/**", "/api/v1//file/download/**").hasAuthority("SCOPE_ADMIN");
                    auth.anyRequest().authenticated();
                })
                .headers(h->h.frameOptions(fo->fo.disable()))
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(oauth2->{
                    oauth2.authenticationEntryPoint(new fujitora.amiral.accountservice.security.CustomOAuth2AuthenticationEntryPoint());
                    oauth2.accessDeniedHandler(new fujitora.amiral.accountservice.security.CustomOAuth2AccessDeniedHandler());
                })
                .userDetailsService(this.userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){

        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(this.passwordEncoder());
        authProvider.setUserDetailsService(this.userDetailsService);

        return new ProviderManager(authProvider);
    }

    @Bean
    public JwtEncoder jwtEncoder(){

        JWK jwk = new RSAKey.Builder(this.rsaConfigProperties.publicKey()).privateKey(this.rsaConfigProperties.privateKey()).build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(this.rsaConfigProperties.publicKey()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}