package TokenUs.TokenUs_BE.config.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.enums.Status;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {

        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(user.getRole()); // ✅ Role 자체가 GrantedAuthority이므로 바로 반환
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // email을 사용자명으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 정책이 없다면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != Status.INACTIVE; // 예: 비활성화된 계정이면 false
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 정책이 없다면 true
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == Status.ACTIVE;
    }
}
