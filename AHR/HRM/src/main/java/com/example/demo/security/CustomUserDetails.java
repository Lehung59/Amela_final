package com.example.demo.security;

import com.example.demo.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String userName;
    private String password;
    private boolean isEnabled;
    private String fullName;
    private String avatar;
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.userName = user.getEmail();
        this.fullName = user.getFirstName()+' '+user.getLastName();
        this.avatar = user.getAvatar();
        this.password = user.getPassword();
        this.isEnabled = user.getIsActived();
        this.authorities = Arrays.stream(user.getRole().name().split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
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
        return isEnabled;
    }
}
