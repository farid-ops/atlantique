package atlantique.cnut.ne.atlantique.security;

import atlantique.cnut.ne.atlantique.records.RsaConfigProperties;
import atlantique.cnut.ne.atlantique.service.UserDetailsServiceImpl;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@EnableConfigurationProperties(RsaConfigProperties.class)
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

                    // Permet l'accès public à ces chemins
                    auth.requestMatchers("/swagger-ui.html","swagger-ui/**","/v3/**",
                            "/api/v1/oauth2/token",
                            "/api/v1/account/otp/**",
                            "/api/v1/account/new").permitAll();
                    auth.anyRequest().authenticated();
                })
                .headers(h->h.frameOptions(fo->fo.disable()))
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(oauth2->{
                    oauth2.authenticationEntryPoint(new CustomOAuth2AuthenticationEntryPoint());
                    oauth2.accessDeniedHandler(new CustomOAuth2AccessDeniedHandler());
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
        JWKSet jwkSet = new JWKSet(jwk);
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

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