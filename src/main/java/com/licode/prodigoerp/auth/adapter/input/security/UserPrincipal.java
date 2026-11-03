package com.licode.prodigoerp.auth.adapter.input.security;


import com.licode.prodigoerp.auth.domain.model.AccountStatus;
import com.licode.prodigoerp.auth.domain.model.User;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal implements UserDetails {

    private final User user;
    private final Collection<GrantedAuthority> authorities;

    public UserPrincipal(User user, Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return AccountStatus.ACTIVE.equals(user.getStatus());
    }

}
