package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.InnameSchemaCreateRequest;
import com.thomas.medicatieinnameapp.dto.InnameSchemaUpdateRequest;
import com.thomas.medicatieinnameapp.model.*;
import com.thomas.medicatieinnameapp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InnameSchemaService {

    private final InnameSchemaRepository repo;
    private final GebruikerRepository gebruikerRepo;
    private final MedicatieRepository medicatieRepo;

    public InnameSchemaService(InnameSchemaRepository repo,
                               GebruikerRepository gebruikerRepo,
                               MedicatieRepository medicatieRepo) {
        this.repo = repo;
        this.gebruikerRepo = gebruikerRepo;
        this.medicatieRepo = medicatieRepo;
    }

    public InnameSchema getByIdOr404(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema niet gevonden"));
    }

    @Transactional
    public InnameSchema create(Long gebruikerId, Long medicatieId, InnameSchemaCreateRequest req) {
        Gebruiker g = gebruikerRepo.findById(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));
        Medicatie m = medicatieRepo.findById(medicatieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden"));

        if (req.getEindDatum() != null && req.getEindDatum().isBefore(req.getStartDatum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eindDatum ligt voor startDatum");
        }

        InnameSchema s = new InnameSchema();
        s.setGebruiker(g);
        s.setMedicatie(m);
        s.setStartDatum(req.getStartDatum());
        s.setEindDatum(req.getEindDatum());
        s.setFrequentiePerDag(req.getFrequentiePerDag() != null ? req.getFrequentiePerDag() : 1);

        return repo.save(s);
    }

    public List<InnameSchema> listByGebruiker(Long gebruikerId) {
        return repo.findByGebruiker_Id(gebruikerId);
    }

    public List<InnameSchema> listByMedicatie(Long medicatieId) {
        return repo.findByMedicatie_Id(medicatieId);
    }

    @Transactional
    public InnameSchema update(Long id, InnameSchemaUpdateRequest req) {
        InnameSchema s = getByIdOr404(id);
        if (req.getEindDatum() != null && req.getEindDatum().isBefore(req.getStartDatum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eindDatum ligt voor startDatum");
        }
        s.setStartDatum(req.getStartDatum());
        s.setEindDatum(req.getEindDatum());
        s.setFrequentiePerDag(req.getFrequentiePerDag());
        return repo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema niet gevonden");
        }
        repo.deleteById(id);
    }
}
