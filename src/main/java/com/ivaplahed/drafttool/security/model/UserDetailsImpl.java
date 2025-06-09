package com.ivaplahed.drafttool.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ivaplahed.drafttool.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    private String username;

    @JsonIgnore
    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @Getter(AccessLevel.NONE)
    private Boolean enabled;

    private Collection<? extends GrantedAuthority> authorities;



    public static UserDetailsImpl build(User user) {

        if (user == null) {
            return null;
        }
        List<GrantedAuthority> grantedAuthorityList = user.getRoles().stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getName().toString()))
                .toList();

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getEnabled(),
                grantedAuthorityList
        );
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
        return this.enabled;
    }
}
