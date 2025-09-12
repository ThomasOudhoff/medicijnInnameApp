package com.thomas.medicatieinnameapp.service;
import com.thomas.medicatieinnameapp.dto.MedicatieUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicatieService {

    private final MedicatieRepository medicatieRepository;
    private final GebruikerRepository gebruikerRepository;   // <-- ontbrekend veld toegevoegd

    public MedicatieService(MedicatieRepository medicatieRepository,
                            GebruikerRepository gebruikerRepository) { // <-- via constructor injecteren
        this.medicatieRepository = medicatieRepository;
        this.gebruikerRepository = gebruikerRepository;
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
        m.setNaamMedicijn(req.getNaam());
        m.setOmschrijving(req.getOmschrijving());
        m.setBijsluiterUrl(req.getBijsluiterUrl());
        return medicatieRepository.save(m);
    }

    @Transactional
    public Medicatie createForGebruiker(Long gebruikerId, String naam) {
        Gebruiker g = gebruikerRepository.findById(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));
        Medicatie m = new Medicatie();
        m.setNaamMedicijn(naam);
        m.setGebruiker(g);
        return medicatieRepository.save(m);
    }

    @Transactional
    public void delete(Long id) {
        if (!medicatieRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden");
        }
        medicatieRepository.deleteById(id);
    }

    public byte[] getBijsluiterFoto(Long id) {
        var m = getByIdOr404(id);
        if (m.getBijsluiterFoto() == null || m.getBijsluiterFoto().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter-foto opgeslagen");
        }
        return m.getBijsluiterFoto();
    }

    @Transactional
    public void saveBijsluiterFoto(Long id, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leeg bestand");
        }
        if (bytes.length > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Bestand is te groot (max 5MB)");
        }
        var m = getByIdOr404(id);
        m.setBijsluiterFoto(bytes);
        medicatieRepository.save(m);
    }

    @Transactional
    public void deleteBijsluiterFoto(Long id) {
        var m = getByIdOr404(id);
        m.setBijsluiterFoto(null);
        medicatieRepository.save(m);
    }

    @Transactional
    public void setBijsluiterUrl(Long id, String url) {
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ongeldige URL");
        }
        var m = getByIdOr404(id);
        m.setBijsluiterUrl(url.trim());
        medicatieRepository.save(m);
    }
}
