package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.MedicatieUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.model.MedicatieBijsluiter;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieBijsluiterRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicatieService {

    private final MedicatieRepository medicatieRepository;
    private final GebruikerRepository gebruikerRepository;
    private final MedicatieBijsluiterRepository bijsluiterRepo;

    public MedicatieService(MedicatieRepository medicatieRepository,
                            GebruikerRepository gebruikerRepository,
                            MedicatieBijsluiterRepository bijsluiterRepo) {
        this.medicatieRepository = medicatieRepository;
        this.gebruikerRepository = gebruikerRepository;
        this.bijsluiterRepo = bijsluiterRepo;
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
        m.setBijsluiterUrl(req.getBijsluiterUrl()); // blijft werken; optioneel als je URL nu via 1-1 opslaat
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

    // ---------- Bijsluiter (1-op-1) ----------
    public byte[] getBijsluiterFoto(Long medicatieId) {
        MedicatieBijsluiter b = bijsluiterRepo.findById(medicatieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter"));
        byte[] data = b.getData();
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter");
        }
        return data;
    }

    @Transactional
    public void saveBijsluiterFoto(Long medicatieId, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leeg bestand");
        }
        if (bytes.length > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Bestand is te groot (max 5MB)");
        }
        Medicatie m = getByIdOr404(medicatieId);
        MedicatieBijsluiter b = bijsluiterRepo.findById(medicatieId).orElseGet(() -> {
            MedicatieBijsluiter nb = new MedicatieBijsluiter();
            nb.setMedicatie(m); // MapsId koppelt medicatie_id
            return nb;
        });
        b.setData(bytes);
        b.setSizeBytes((long) bytes.length);
        b.setContentType("application/octet-stream");
        bijsluiterRepo.save(b);
    }

    @Transactional
    public void deleteBijsluiterFoto(Long medicatieId) {
        if (!bijsluiterRepo.existsById(medicatieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter");
        }
        bijsluiterRepo.deleteById(medicatieId);
    }

    @Transactional
    public void setBijsluiterUrl(Long medicatieId, String url) {
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ongeldige URL");
        }
        Medicatie m = getByIdOr404(medicatieId);
        MedicatieBijsluiter b = bijsluiterRepo.findById(medicatieId).orElseGet(() -> {
            MedicatieBijsluiter nb = new MedicatieBijsluiter();
            nb.setMedicatie(m);
            return nb;
        });
        b.setUrl(url.trim());
        bijsluiterRepo.save(b);
    }
}
