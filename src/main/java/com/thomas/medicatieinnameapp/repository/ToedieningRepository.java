package com.thomas.medicatieinnameapp.repository;

import com.thomas.medicatieinnameapp.model.Toediening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ToedieningRepository extends JpaRepository<Toediening, Long> {
    List<Toediening> findByGebruiker_Id(Long gebruikerId);
    List<Toediening> findByMedicatie_Id(Long medicatieId);
    List<Toediening> findBySchemaInname_Id(Long schemaId);

    List<Toediening> findByGebruiker_IdAndTijdstipBetween(Long gebruikerId, LocalDateTime from, LocalDateTime to);
    List<Toediening> findByMedicatie_IdAndTijdstipBetween(Long medicatieId, LocalDateTime from, LocalDateTime to);
}
