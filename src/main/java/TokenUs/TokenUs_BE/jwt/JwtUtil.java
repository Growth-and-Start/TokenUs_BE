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

    public String generateToken(
            String email,
            Collection<? extends GrantedAuthority> authorities,
            long expirationTime,
            String tokenType) {

        Date now = new Date();

        List<String> roleNames = authorities.stream().map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .claim("type", tokenType)
                .claim("roles", roleNames)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    public String generateRefreshToken(
            String email, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();

        List<String> roleNames = authorities.stream().map(GrantedAuthority::getAuthority).toList();

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

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> getRolesFromToken(String token) {

        Claims claims = getAllClaimsFromToken(token);

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public String getTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("type", String.class);
    }
}
