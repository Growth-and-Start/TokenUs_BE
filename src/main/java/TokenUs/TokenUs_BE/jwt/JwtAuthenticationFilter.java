package TokenUs.TokenUs_BE.jwt;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

// jwt를 사용해 사용자 인증 정보를 확인하고, SecurityContextHolder에 저장하는 역할
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 필터에서 JWT 관련 작업을 수행할 때 jwtUtil을 사용
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {

        log.info("JwtAuthenticationFilter 실행됨. 요청 URI: {}", request.getRequestURI());

        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            log.warn("Authorization 헤더가 없거나 JWT 토큰이 없습니다. 요청 URI: {}", request.getRequestURI());
        } else {
            log.info("JWT 토큰 추출 성공: {}", token);

            if (!jwtUtil.validateToken(token)) {
                log.warn("유효하지 않은 JWT 토큰: {}", token);
            } else {
                log.info("JWT 토큰 유효함");

                String email = jwtUtil.getEmailFromToken(token);
                log.info("토큰에서 추출한 사용자 이메일: {}", email);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("SecurityContextHolder에 인증 정보 저장 완료. 사용자: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
