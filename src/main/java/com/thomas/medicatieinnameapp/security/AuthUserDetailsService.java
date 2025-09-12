package com.thomas.medicatieinnameapp.security;

import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final GebruikerRepository repo;
    public AuthUserDetailsService(GebruikerRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var g = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Geen gebruiker met email: " + email));
        return new AuthUser(g);
    }
}
