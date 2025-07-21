package backend.recimeclone.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mixit Recime Clone API",
                version = "1.0",
                description = "API documentation for the Mixit Recime Clone application.",
                contact = @Contact(
                        name = "Mixit Team",
                        email = "support@mixit.com" //support email
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://api.recime-app.com", description = "Production Server")
        }
)
@SecurityScheme(
        name = "bearerAuth", // Name of the security scheme
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT authentication using a Bearer token. Add your JWT token here."
)
public class OpenApiConfig {
    // This class primarily uses annotations to configure OpenAPI.
    // No additional methods are typically needed here for basic setup.
}
