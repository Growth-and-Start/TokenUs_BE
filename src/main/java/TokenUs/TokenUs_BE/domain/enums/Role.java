package TokenUs.TokenUs_BE.domain.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    USER;

    // ROLE 자체가 GrantedAuthority임
    @Override
    public String getAuthority() {
        return "ROLE_" + name(); // "ROLE_USER", "ROLE_ADMIN" 형태로 반환
    }
}
