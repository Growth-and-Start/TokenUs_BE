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

        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // SecuritySchemes 등록
        Components componets =
                new Components()
                        .addSecuritySchemes(
                                jwtSchemeName,
                                new SecurityScheme()
                                        .name(jwtSchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"));

        // OpenAPI 객체 생성 및 설정
        return new OpenAPI()
                .addServersItem(new Server().url("/")) // 서버 정보
                .info(info) // API 정보
                .addSecurityItem(securityRequirement)
                .components(componets);
    }
}
