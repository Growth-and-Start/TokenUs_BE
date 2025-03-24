package TokenUs.TokenUs_BE.config;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.config.security.CustomUserDetailsService;
import TokenUs.TokenUs_BE.jwt.JwtAuthenticationFilter;
import TokenUs.TokenUs_BE.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            JwtUtil jwtUtil,
            ObjectMapper objectMapper,
            CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.customUserDetailsService = customUserDetailsService;
    }

    // AuthenticationManager 를 빈으로 등록 (스프링 시큐리티 6.x 이상)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
        http.cors(Customizer.withDefaults())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/auth/login",
                                                "/auth/signup",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/",
                                                "video/similarity-check",
                                                "/auth/email_check/**",
                                                "s3/presigned-url/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, customUserDetailsService),
                        org.springframework.security.web.authentication
                                .UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        private final ObjectMapper objectMapper;

        public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void commence(
                HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException)
                throws IOException, ServletException {

            ApiResponse<Void> apiResponse =
                    ApiResponse.onFailure(
                            ErrorStatus.NOT_AUTHORIZED.getCode(),
                            ErrorStatus.NOT_AUTHORIZED.getMessage(),
                            null);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(jsonResponse);
        }
    }
}
