package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.InnameSchemaCreateRequest;
import com.thomas.medicatieinnameapp.dto.InnameSchemaResponse;
import com.thomas.medicatieinnameapp.dto.InnameSchemaUpdateRequest;
import com.thomas.medicatieinnameapp.model.InnameSchema;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.InnameSchemaRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InnameSchemaService {

    private final InnameSchemaRepository repo;
    private final MedicatieRepository medicatieRepo;

    public InnameSchemaService(InnameSchemaRepository repo,
                               MedicatieRepository medicatieRepo) {
        this.repo = repo;
        this.medicatieRepo = medicatieRepo;
    }

    public InnameSchema getByIdOr404(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema niet gevonden"));
    }

    @Transactional
    public InnameSchema create(Long medicatieId, InnameSchemaCreateRequest req) {
        Medicatie m = medicatieRepo.findById(medicatieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden"));

        if (req.getStartDatum() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDatum is verplicht");
        }
        if (req.getEindDatum() != null && req.getEindDatum().isBefore(req.getStartDatum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eindDatum ligt voor startDatum");
        }

        InnameSchema s = new InnameSchema();
        s.setMedicatie(m);
        s.setGebruiker(m.getGebruiker()); // eigenaar = eigenaar van medicatie
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

        if (req.getStartDatum() != null) {
            s.setStartDatum(req.getStartDatum());
        }
        if (req.getEindDatum() != null) {
            s.setEindDatum(req.getEindDatum());
        }
        if (req.getFrequentiePerDag() != null) {
            s.setFrequentiePerDag(req.getFrequentiePerDag());
        }

        if (s.getStartDatum() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDatum mag niet leeg zijn");
        }
        if (s.getEindDatum() != null && s.getEindDatum().isBefore(s.getStartDatum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eindDatum ligt voor startDatum");
        }

        return repo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema niet gevonden");
        }
        repo.deleteById(id);
    }

    public InnameSchemaResponse map(InnameSchema s) {
        InnameSchemaResponse dto = new InnameSchemaResponse();
        dto.setId(s.getId());
        dto.setMedicatieId(s.getMedicatie() != null ? s.getMedicatie().getId() : null);
        dto.setGebruikerId(s.getGebruiker() != null ? s.getGebruiker().getId() : null);
        dto.setStartDatum(s.getStartDatum());
        dto.setEindDatum(s.getEindDatum());
        dto.setFrequentiePerDag(s.getFrequentiePerDag());
        return dto;
    }
}
