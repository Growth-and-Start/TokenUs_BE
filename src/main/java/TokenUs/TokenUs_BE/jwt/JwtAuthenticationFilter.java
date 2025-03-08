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

// jwt를 사용해 사용자 인증 정보를 확인하고, SecurityContextHolder에 저장하는 역할
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

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmailFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
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

    //    @Override
    //    protected void doFilterInternal(
    //            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    //            throws IOException, jakarta.servlet.ServletException {
    //
    //        // 1. Authorization 헤더 파싱
    //        String authHeader = request.getHeader("Authorization");
    //
    //        if (authHeader != null && authHeader.startsWith("Bearer ")) {
    //            String token = authHeader.substring(7);
    //
    //            // 2. 토큰 유효성 검사
    //            if (jwtUtil.validateToken(token)
    //                    && (jwtUtil.getTypeFromToken(token).equals("access"))) {
    //
    //                // 3. 토큰에서 사용자 정보 추출
    //                String email = jwtUtil.getEmailFromToken(token);
    //
    //                // 4. UserDetailsService를 사용하여 사용자 정보 조회
    //                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    //
    //                // 5. Spring Security 인증 객체 생성
    //                UsernamePasswordAuthenticationToken authentication =
    //                        new UsernamePasswordAuthenticationToken(userDetails, null,
    // userDetails.getAuthorities());
    //
    //                // 6. SecurityContextHolder에 인증 정보 저장
    //                SecurityContextHolder.getContext().setAuthentication(authentication);
    //            }
    //        }
    //
    //        // 필터 체인 계속 진행(다음필터로 요청 넘기기)
    //        filterChain.doFilter(request, response);
    //    }

}
