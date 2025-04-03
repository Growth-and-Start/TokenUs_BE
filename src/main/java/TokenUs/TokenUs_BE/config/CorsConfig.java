package TokenUs.TokenUs_BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                        .allowedOrigins(
                                "http://localhost:5173", // 로컬 프론트엔드 개발 환경, 배포 후 변경 필요
                                "http://localhost:5500" // 로컬 웹소캣 테스트환경
                                )
                        .allowedMethods("*") // ✅ 모든 HTTP 메서드 허용
                        .allowedHeaders("*") // ✅ 모든 요청 헤더 허용
                        .allowCredentials(true) // JWT 쿠키/토큰 포함 허용, allowOrigins("*")불가
                        .exposedHeaders("Authorization"); // ✅ JWT 토큰이 담긴 응답 헤더 허용
            }
        };
    }
}
