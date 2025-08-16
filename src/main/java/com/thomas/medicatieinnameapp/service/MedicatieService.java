package com.thomas.medicatieinnameapp.service;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class MedicatieService {
    private final MedicatieRepository medicatieRepository;

    public MedicatieService(MedicatieRepository medicatieRepository) {
        this.medicatieRepository = medicatieRepository;
    }

    public Medicatie saveMedicatie(Medicatie medicatie) {
        return medicatieRepository.save(medicatie);
    }

    public List<Medicatie> getAllMedicatie() {
        return medicatieRepository.findAll();
    }
}
