package TokenUs.TokenUs_BE.jwt;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String SECRET_KEY;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Key getSigningKey() {
        return this.key;
    }

    // JWT 액세스 토큰 생성
    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities) {

        Date now = new Date();

        List<String> roleNames =
                authorities.stream()
                        .map(GrantedAuthority::getAuthority) // "ROLE_ADMIN" 등
                        .toList();

        // 30분 만료
        long EXPIRATION_TIME = 1000 * 60 * 30L;

        return Jwts.builder()
                .setSubject(email) // 사용자 식별 정보
                .setIssuedAt(now) // 발긃 시간
                .claim("type", "access") // type: 토큰 종류
                .claim("roles", roleNames)
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME)) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // HS256 알고리즘
                .compact();
    }

    // 토큰에서 이메일(=username)추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 만료되었거나 서명 검증 실패 등
            return false;
        }
    }

    // Jwt 리프레시 토큰 생성
    public String generateRefreshToken(
            String email, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();

        List<String> roleNames = authorities.stream().map(GrantedAuthority::getAuthority).toList();

        // expiration 1일
        long EXPIRATION_TIME = 1000 * 60 * 60 * 24L;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .claim("type", "refresh")
                .claim("roles", roleNames)
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 클레임 요청
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 role 반환
    public List<String> getRolesFromToken(String token) {

        Claims claims = getAllClaimsFromToken(token);

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream().map(Object::toString).toList();
        }
        return List.of(); // 빈 리스트 반환
    }

    // 토큰에서 type 추출, accessToken인지 refreshToken인지
    public String getTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("type", String.class);
    }
}
