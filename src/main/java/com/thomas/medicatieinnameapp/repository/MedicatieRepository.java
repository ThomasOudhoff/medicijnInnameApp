package com.thomas.medicatieinnameapp.repository;
import com.thomas.medicatieinnameapp.model.Medicatie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicatieRepository extends JpaRepository<Medicatie, Long> {
}

