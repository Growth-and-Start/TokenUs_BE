package TokenUs.TokenUs_BE.jwt;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import TokenUs.TokenUs_BE.config.security.CustomUserDetails;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.enums.Role;
import TokenUs.TokenUs_BE.repository.UserRepository;

// jwt를 사용해 사용자 인증 정보를 확인하고, SecurityContextHolder에 저장하는 역할
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 필터에서 JWT 관련 작업을 수행할 때 jwtUtil을 사용
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {

        // 1. Authorization 헤더 파싱
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. 토큰 유효성 검사
            if (jwtUtil.validateToken(token)
                    && (jwtUtil.getTypeFromToken(token).equals("access"))) {

                // 3. 토큰에서 사용자 정보 추출
                String email = jwtUtil.getEmailFromToken(token);

                // 4. DB에서 사용자 조회
                User user =
                        userRepository
                                .findByEmail(email) // DB에서 User 객체 조회
                                .orElseThrow(
                                        () ->
                                                new UsernameNotFoundException(
                                                        "User not found: " + email));

                // 5. 단일 Role을 리스트로 변환 (SimpleGrantedAuthority 없이 직접 사용)
                List<Role> authorities = List.of(user.getRole()); // ✅ 변경된 부분

                // CustomUsrDetails 객체 생성 후 저장
                CustomUserDetails userDetails = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 필터 체인 계속 진행(다음필터로 요청 넘기기)
        filterChain.doFilter(request, response);
    }
}
