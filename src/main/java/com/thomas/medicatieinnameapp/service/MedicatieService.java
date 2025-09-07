package com.thomas.medicatieinnameapp.service;
import com.thomas.medicatieinnameapp.dto.MedicatieUpdateRequest;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Medicatie getByIdOr404(Long id) {
        return medicatieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden"));
    }

    @Transactional
    public Medicatie update(Long id, MedicatieUpdateRequest req) {
        Medicatie m = getByIdOr404(id);
        m.setNaamMedicijn(req.getNaam());                 // pas aan als jouw entity anders heet
        m.setOmschrijving(req.getOmschrijving());
        m.setBijsluiterUrl(req.getBijsluiterUrl());
        return medicatieRepository.save(m);
    }

    @Transactional
    public void delete(Long id) {
        if (!medicatieRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden");
        }
        medicatieRepository.deleteById(id);
    }
}
