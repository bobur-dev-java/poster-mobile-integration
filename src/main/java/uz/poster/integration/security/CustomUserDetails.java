package uz.poster.integration.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.poster.integration.user.entity.User;
import uz.poster.integration.user.entity.enums.UserRole;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String id;
    private final String phone;
    private final UserRole role;
    private final boolean banned;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.banned = user.getBannedAt() != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !banned;
    }
}
