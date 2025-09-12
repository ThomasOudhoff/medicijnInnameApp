package com.thomas.medicatieinnameapp.security;

import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Rol;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AuthUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Rol rol;

    public AuthUser(Gebruiker g) {

        this.id = g.getId();
        this.email = g.getEmail();
        this.password = g.getWachtwoord();
        this.rol = g.getRol();
    }

    public Long getId() {
        return id; }
    public Rol getRol() {
        return rol; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override public String getPassword() {
        return password; }
    @Override public String getUsername() {
        return email; }

    @Override public boolean isAccountNonExpired() {
        return true; }
    @Override public boolean isAccountNonLocked() {
        return true; }
    @Override public boolean isCredentialsNonExpired() {
        return true; }
    @Override public boolean isEnabled() {
        return true; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthUser)) return false;
        AuthUser that = (AuthUser) o;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() {
        return Objects.hash(id); }

}
