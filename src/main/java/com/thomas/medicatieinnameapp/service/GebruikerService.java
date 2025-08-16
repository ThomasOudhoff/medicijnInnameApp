package com.thomas.medicatieinnameapp.service;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import org.springframework.stereotype.Service;
@Service
public class GebruikerService {
    private final GebruikerRepository gebruikerRepository;

    public GebruikerService(GebruikerRepository gebruikerRepository) {
        this.gebruikerRepository = gebruikerRepository;
    }
    public Gebruiker saveGebruiker(Gebruiker gebruiker) {
        return gebruikerRepository.save(gebruiker);
    }
}
