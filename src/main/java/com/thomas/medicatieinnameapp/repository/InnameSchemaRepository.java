package com.thomas.medicatieinnameapp.repository;

import com.thomas.medicatieinnameapp.model.InnameSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InnameSchemaRepository extends JpaRepository<InnameSchema, Long> {
    List<InnameSchema> findByGebruiker_Id(Long gebruikerId);
    List<InnameSchema> findByMedicatie_Id(Long medicatieId);
}
