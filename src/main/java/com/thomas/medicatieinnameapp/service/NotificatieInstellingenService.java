package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.NotificatieInstellingenRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.NotificatieInstellingen;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.NotificatieInstellingenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificatieInstellingenService {

    private final NotificatieInstellingenRepository repo;
    private final GebruikerRepository gebruikerRepo;

    public NotificatieInstellingenService(NotificatieInstellingenRepository repo, GebruikerRepository gebruikerRepo) {
        this.repo = repo;
        this.gebruikerRepo = gebruikerRepo;
    }

    public NotificatieInstellingen getByGebruikerIdOr404(Long gebruikerId) {
        return repo.findByGebruikerId(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instellingen niet gevonden"));
    }

    @Transactional
    public NotificatieInstellingen upsert(Long gebruikerId, NotificatieInstellingenRequest req) {
        Gebruiker g = gebruikerRepo.findById(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));

        NotificatieInstellingen n = repo.findByGebruikerId(gebruikerId).orElseGet(() -> {
            NotificatieInstellingen nieuw = new NotificatieInstellingen();
            nieuw.setGebruiker(g);
            return nieuw;
        });

        n.setStilleStart(req.getStilleStart());
        n.setStilleEinde(req.getStilleEinde());
        n.setKanaal(req.getKanaal());
        n.setMinutenVooraf(req.getMinutenVooraf());
        n.setSnoozeMinuten(req.getSnoozeMinuten());
        n.setActief(req.getActief());

        return repo.save(n);
    }

    @Transactional
    public void deleteByGebruikerId(Long gebruikerId) {
        if (!repo.existsByGebruikerId(gebruikerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instellingen niet gevonden");
        }
        repo.deleteByGebruikerId(gebruikerId);
    }
}
