package atlantique.cnut.ne.atlantique.security;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("ALL")
public class JwtUtils {
    private final JwtEncoder jwtEncoder;

    public JwtUtils(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }


    public String generateToken(Utilisateur utilisateur, Collection<? extends GrantedAuthority> authorities, long expirationSeconds, String tokenType) {
        Instant now = Instant.now();
        String scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .subject(utilisateur.getId())
                .claim("scope", scope)
                .claim("type", tokenType);

        claimsBuilder.claim("id", utilisateur.getId());
        claimsBuilder.claim("nom", utilisateur.getNom());
        claimsBuilder.claim("prenom", utilisateur.getPrenom());
        claimsBuilder.claim("email", utilisateur.getEmail());
        claimsBuilder.claim("phone", utilisateur.getTelephone());
        claimsBuilder.claim("idSite", utilisateur.getIdSite());
        claimsBuilder.claim("idPays", utilisateur.getIdPays());
        claimsBuilder.claim("idGroupe", utilisateur.getIdGroupe());

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }


}