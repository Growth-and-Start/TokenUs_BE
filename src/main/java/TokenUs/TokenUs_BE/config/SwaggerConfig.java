package TokenUs.TokenUs_BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI TokenusAPI() {
        Info info = new Info().title("TokenUs API").description("TokenUS API 명세서").version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";

        // Containing authentication information in the API request header
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // Define SecuritySchemes
        Components componets =
                new Components()
                        .addSecuritySchemes(
                                jwtSchemeName,
                                new SecurityScheme()
                                        .name(jwtSchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"));

        // Create and configure OpenAPI object
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(componets);
    }
}
