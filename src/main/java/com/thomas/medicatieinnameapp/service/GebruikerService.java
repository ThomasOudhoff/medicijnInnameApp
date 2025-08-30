package com.thomas.medicatieinnameapp.service;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Optional;

@Service
public class GebruikerService {
    private final GebruikerRepository gebruikerRepository;

    public GebruikerService(GebruikerRepository gebruikerRepository) {
        this.gebruikerRepository = gebruikerRepository;
    }
    public Gebruiker saveGebruiker(Gebruiker gebruiker) {
        return gebruikerRepository.save(gebruiker);
    }
    public List<Gebruiker> getAllGebruikers() {
        return gebruikerRepository.findAll();
    }
    public Optional<Gebruiker> getGebruikerById(Long id) {
        return gebruikerRepository.findById(id);
    }

}
