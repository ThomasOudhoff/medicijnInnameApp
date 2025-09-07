package com.thomas.medicatieinnameapp.repository;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GebruikerRepository extends JpaRepository<Gebruiker, Long> {
    Optional<Gebruiker> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByEmailIgnoreCase(String email);
}
