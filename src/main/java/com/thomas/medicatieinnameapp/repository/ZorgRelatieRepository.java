package com.thomas.medicatieinnameapp.repository;

import com.thomas.medicatieinnameapp.model.ZorgRelatie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZorgRelatieRepository extends JpaRepository<ZorgRelatie, Long> {
    boolean existsByVerzorger_IdAndGebruiker_Id(Long verzorgerId, Long gebruikerId);
    long deleteByVerzorger_IdAndGebruiker_Id(Long verzorgerId, Long gebruikerId);
    Optional<ZorgRelatie> findByVerzorger_IdAndGebruiker_Id(Long verzorgerId, Long gebruikerId);
}
