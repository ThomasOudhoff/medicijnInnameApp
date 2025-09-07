package com.thomas.medicatieinnameapp.repository;

import com.thomas.medicatieinnameapp.model.Medicatie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicatieRepository extends JpaRepository<Medicatie, Long> {

    List<Medicatie> findByGebruikerId(Long gebruikerId);
    boolean existsByGebruikerId(Long gebruikerId);
}

