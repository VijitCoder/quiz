package engine.dto;

import engine.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

public class UserDetailsDto implements UserDetails {
    private static final String USER_ROLE = "user";

    private final String email;

    private final String password;

    /**
     * Hardcoded roles because for now we don't need this feature in more complicated view.
     */
    private final List<SimpleGrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority(USER_ROLE)
    );

    public UserDetailsDto(User user) {
        email = user.getEmail();
        password = user.getPassword();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
