package com.example.user_app.security;

import com.example.user_app.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
public class UserDetailsImpl implements UserDetails {
    private UUID userId;
    private String username;
    private String email;
    private String password;
    private boolean isActive;
    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(UUID userId, String username, String email, String password, List<GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDetailsImpl(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public static UserDetails build(User user){
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
              user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), authorities
        );
    }

    //oauth2 google
    public static UserDetailsImpl build(OidcUser oidcUser){
        List<GrantedAuthority> authorities = oidcUser.getAuthorities().stream()
                .map(authority -> (GrantedAuthority) authority)
                .collect(Collectors.toList());
        return new UserDetailsImpl(oidcUser.getEmail(), authorities);
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
