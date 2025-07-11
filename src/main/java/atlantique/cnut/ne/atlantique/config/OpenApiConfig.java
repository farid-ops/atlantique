package atlantique.cnut.ne.atlantique.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Atlantique",
                version = "1.0.0",
                description = "Documentation pour les APIs de gestion du système Atlantique.",
                termsOfService = "https://www.atlantique.cnut.ne/terms",
                contact = @Contact(
                        name = "Support Atlantique",
                        email = "support@atlantique.cnut.ne",
                        url = "https://www.atlantique.cnut.ne/contact"
                ),
                license = @License(
                        name = "Licence Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:7070",
                        description = "Serveur de développement local"
                ),
                @Server(
                        url = "https://www.atlantique.cnut.ne",
                        description = "Serveur de production"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Authentification JWT Requise. Entrez votre jeton Bearer ici."
)
public class OpenApiConfig {
}