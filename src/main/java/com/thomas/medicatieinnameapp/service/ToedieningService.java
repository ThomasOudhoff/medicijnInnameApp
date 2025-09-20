package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.ToedieningCreateRequest;
import com.thomas.medicatieinnameapp.model.*;
import com.thomas.medicatieinnameapp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ToedieningService {

    private final ToedieningRepository repo;
    private final GebruikerRepository gebruikerRepo;
    private final MedicatieRepository medicatieRepo;
    private final InnameSchemaRepository schemaRepo;


    public ToedieningService(ToedieningRepository repo,
                             GebruikerRepository gebruikerRepo,
                             MedicatieRepository medicatieRepo,
                             InnameSchemaRepository schemaRepo) {
        this.repo = repo;
        this.gebruikerRepo = gebruikerRepo;
        this.medicatieRepo = medicatieRepo;
        this.schemaRepo = schemaRepo;
    }

    public Toediening getByIdOr404(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Toediening niet gevonden"));
    }

    @Transactional
    public Toediening create(Long gebruikerId, Long medicatieId, ToedieningCreateRequest req) {
        Gebruiker g = gebruikerRepo.findById(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));
        Medicatie m = medicatieRepo.findById(medicatieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden"));

        // medicatie moet bij deze gebruiker horen
        if (!m.getGebruiker().getId().equals(g.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicatie hoort niet bij deze gebruiker");
        }

        InnameSchema schema = null;
        if (req.getSchemaId() != null) {
            schema = schemaRepo.findById(req.getSchemaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema niet gevonden"));
            // extra guard: schema moet bij dezelfde gebruiker/medicatie horen
            if (!schema.getGebruiker().getId().equals(g.getId()) ||
                    !schema.getMedicatie().getId().equals(m.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Schema hoort niet bij gebruiker/medicatie");
            }
        }

        Toediening t = new Toediening();
        t.setGebruiker(g);
        t.setMedicatie(m);
        t.setSchemaInname(schema);
        t.setTijdstip(req.getTijdstip() != null ? req.getTijdstip() : LocalDateTime.now());
        t.setHoeveelheid(req.getHoeveelheid());
        t.setOpmerking(req.getOpmerking());

        return repo.save(t);
    }

    public List<Toediening> listByGebruiker(Long gebruikerId, LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(LocalTime.MAX);
            return repo.findByGebruiker_IdAndTijdstipBetween(gebruikerId, start, end);
        }
        return repo.findByGebruiker_Id(gebruikerId);
    }

    public List<Toediening> listByMedicatie(Long medicatieId, LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(LocalTime.MAX);
            return repo.findByMedicatie_IdAndTijdstipBetween(medicatieId, start, end);
        }
        return repo.findByMedicatie_Id(medicatieId);
    }

    public List<Toediening> listBySchema(Long schemaId) {
        return repo.findBySchemaInname_Id(schemaId);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Toediening niet gevonden");
        }
        repo.deleteById(id);
    }
    @Transactional
    public Toediening createForSchema(Long schemaId, ToedieningCreateRequest req) {
        var schema = schemaRepo.findById(schemaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema niet gevonden"));

        Toediening t = new Toediening();
        t.setGebruiker(schema.getGebruiker());
        t.setMedicatie(schema.getMedicatie());
        t.setSchemaInname(schema);
        t.setTijdstip(req.getTijdstip());
        t.setHoeveelheid(req.getHoeveelheid());
        t.setOpmerking(req.getOpmerking());

        return repo.save(t);
    }
}
