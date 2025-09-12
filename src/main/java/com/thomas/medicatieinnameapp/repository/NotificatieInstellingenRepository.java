package com.thomas.medicatieinnameapp.repository;

import com.thomas.medicatieinnameapp.model.NotificatieInstellingen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificatieInstellingenRepository extends JpaRepository<NotificatieInstellingen, Long> {
    Optional<NotificatieInstellingen> findByGebruikerId(Long gebruikerId);
    boolean existsByGebruikerId(Long gebruikerId);
    void deleteByGebruikerId(Long gebruikerId);
}
