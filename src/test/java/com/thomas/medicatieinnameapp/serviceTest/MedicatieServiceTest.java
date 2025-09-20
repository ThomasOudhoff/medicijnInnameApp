package com.thomas.medicatieinnameapp.serviceTest;

import com.thomas.medicatieinnameapp.service.MedicatieService;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieBijsluiterRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MedicatieServiceTest {

    private MedicatieRepository medRepo;
    private GebruikerRepository gebRepo;
    private MedicatieBijsluiterRepository bijRepo;
    private MedicatieService service;

    @BeforeEach
    void setUp() {
        medRepo = mock(MedicatieRepository.class);
        gebRepo = mock(GebruikerRepository.class);
        bijRepo = mock(MedicatieBijsluiterRepository.class);
        service = new MedicatieService(medRepo, gebRepo, bijRepo);
    }

    // -- kleine helper om snel een entity te maken
    private Medicatie mkMedicatie(Long id, Long gebruikerId, String naam, String url) {
        Medicatie m = new Medicatie();
        m.setId(id);
        m.setNaamMedicijn(naam);
        m.setBijsluiterUrl(url);
        if (gebruikerId != null) {
            Gebruiker g = new Gebruiker();
            g.setId(gebruikerId);
            m.setGebruiker(g);
        }
        return m;
    }

    // 1
    @Test
    @DisplayName("getByIdOr404: not found → ResponseStatusException")
    void getByIdOr404_notFound() {
        when(medRepo.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByIdOr404(999L))
                .isInstanceOf(ResponseStatusException.class);
        verify(medRepo).findById(999L);
        verifyNoMoreInteractions(medRepo, gebRepo, bijRepo);
    }

    // 2
    @Test
    @DisplayName("getByIdOr404: found → zelfde entity terug")
    void getByIdOr404_found_ok() {
        Medicatie bestaand = mkMedicatie(1L, 10L, "Paracetamol", null);
        when(medRepo.findById(1L)).thenReturn(Optional.of(bestaand));
        Medicatie result = service.getByIdOr404(1L);
        assertThat(result).isSameAs(bestaand);
        verify(medRepo).findById(1L);
        verifyNoMoreInteractions(medRepo, gebRepo, bijRepo);
    }

    // 3
    @Test
    @DisplayName("createForGebruiker: ok → opgeslagen object met juiste gebruiker/naam")
    void createForGebruiker_ok() {
        Long gebruikerId = 10L;
        Gebruiker gebruiker = new Gebruiker();
        gebruiker.setId(gebruikerId);
        when(gebRepo.findById(gebruikerId)).thenReturn(Optional.of(gebruiker));
        when(medRepo.save(any(Medicatie.class))).thenAnswer(inv -> inv.getArgument(0));

        Medicatie created = service.createForGebruiker(gebruikerId, "Ibuprofen");

        assertThat(created).isNotNull();
        assertThat(created.getNaamMedicijn()).isEqualTo("Ibuprofen");
        assertThat(created.getGebruiker()).isNotNull();
        assertThat(created.getGebruiker().getId()).isEqualTo(gebruikerId);

        ArgumentCaptor<Medicatie> captor = ArgumentCaptor.forClass(Medicatie.class);
        verify(medRepo).save(captor.capture());
        Medicatie saved = captor.getValue();
        assertThat(saved.getNaamMedicijn()).isEqualTo("Ibuprofen");
        assertThat(saved.getGebruiker().getId()).isEqualTo(gebruikerId);

        verify(gebRepo).findById(gebruikerId);
        verifyNoMoreInteractions(medRepo, gebRepo, bijRepo);
    }

    // 4
    @Test
    void createForGebruiker_missingNaam_wordtGewoonAangemaakt() {
        Long gebruikerId = 10L;
        Gebruiker g = new Gebruiker(); g.setId(gebruikerId);
        when(gebRepo.findById(gebruikerId)).thenReturn(Optional.of(g));
        when(medRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Medicatie created = service.createForGebruiker(gebruikerId, null);

        assertThat(created.getGebruiker().getId()).isEqualTo(gebruikerId);
        assertThat(created.getNaamMedicijn()).isNull(); // huidige gedrag
        verify(medRepo).save(any(Medicatie.class));
    }
    // 5
    @Test
    @DisplayName("delete: ok → repo.deleteById aangeroepen")
    void delete_ok() {
        Long id = 7L;
        when(medRepo.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(medRepo).existsById(id);
        verify(medRepo).deleteById(id);
        verifyNoMoreInteractions(medRepo, gebRepo, bijRepo);
    }

    // 6
    @Test
    @DisplayName("delete: not found → 404/Not Found")
    void delete_notFound_404() {
        Long id = 8L;
        when(medRepo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(ResponseStatusException.class);

        verify(medRepo).existsById(id);
        verifyNoMoreInteractions(medRepo, gebRepo, bijRepo);
    }
    // 8
    @Test
    void setBijsluiterUrl_invalid_badRequest() {
        Long id = 13L;
        assertThatThrownBy(() -> service.setBijsluiterUrl(id, null))
                .isInstanceOf(ResponseStatusException.class);

        assertThatThrownBy(() -> service.setBijsluiterUrl(id, "   "))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(medRepo, bijRepo, gebRepo);
    }
}

