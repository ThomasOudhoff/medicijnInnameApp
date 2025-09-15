package com.thomas.medicatieinnameapp.serviceTest;

import com.thomas.medicatieinnameapp.dto.GebruikerUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Rol;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.service.GebruikerService;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GebruikerServiceTest {

    private GebruikerRepository gebruikerRepository;
    private MedicatieRepository medicatieRepository;
    private GebruikerService service;

    @BeforeEach
    void setUp() {
        gebruikerRepository = mock(GebruikerRepository.class);
        medicatieRepository  = mock(MedicatieRepository.class);
        service = new GebruikerService(gebruikerRepository, medicatieRepository);
    }

    private Gebruiker mkGebruiker(Long id, String naam, String email, Rol rol) {
        Gebruiker g = new Gebruiker();
        g.setId(id);
        g.setNaam(naam);
        g.setEmail(email);
        g.setRol(rol);
        return g;
    }

    // 1
    @Test
    @DisplayName("getByIdOrThrow: gevonden → entity terug")
    void getByIdOrThrow_found_ok() {
        var bestaand = mkGebruiker(1L, "Alice", "alice@example.com", Rol.GEBRUIKER);
        when(gebruikerRepository.findById(1L)).thenReturn(Optional.of(bestaand));

        var result = service.getByIdOrThrow(1L);

        assertThat(result).isSameAs(bestaand);
        verify(gebruikerRepository).findById(1L);
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 2
    @Test
    @DisplayName("getByIdOrThrow: niet gevonden → 404")
    void getByIdOrThrow_notFound_404() {
        when(gebruikerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdOrThrow(999L))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepository).findById(999L);
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 3
    @Test
    @DisplayName("saveGebruiker: OK → unieke e-mail, opgeslagen")
    void saveGebruiker_ok() {
        var nieuw = mkGebruiker(null, "Bob", "bob@example.com", Rol.VERZORGER);
        when(gebruikerRepository.existsByEmailIgnoreCase("bob@example.com")).thenReturn(false);
        when(gebruikerRepository.save(any(Gebruiker.class))).thenAnswer(inv -> {
            Gebruiker g = inv.getArgument(0);
            g.setId(42L);
            return g;
        });

        var saved = service.saveGebruiker(nieuw);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNaam()).isEqualTo("Bob");
        assertThat(saved.getEmail()).isEqualTo("bob@example.com");
        assertThat(saved.getRol()).isEqualTo(Rol.VERZORGER);

        verify(gebruikerRepository).existsByEmailIgnoreCase("bob@example.com");
        verify(gebruikerRepository).save(any(Gebruiker.class));
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 4
    @Test
    @DisplayName("saveGebruiker: e-mail al in gebruik → 409/Conflict")
    void saveGebruiker_emailConflict_409() {
        var nieuw = mkGebruiker(null, "Dup", "dup@example.com", Rol.GEBRUIKER);
        when(gebruikerRepository.existsByEmailIgnoreCase("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.saveGebruiker(nieuw))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepository).existsByEmailIgnoreCase("dup@example.com");
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 5
    @Test
    @DisplayName("updateGebruiker: OK → velden aangepast en opgeslagen")
    void updateGebruiker_ok() {
        Long id = 7L;
        var bestaand = mkGebruiker(id, "Oud", "old@example.com", Rol.GEBRUIKER);
        when(gebruikerRepository.findById(id)).thenReturn(Optional.of(bestaand));
        when(gebruikerRepository.existsByEmailIgnoreCaseAndIdNot("nieuw@example.com", id)).thenReturn(false);
        when(gebruikerRepository.save(any(Gebruiker.class))).thenAnswer(inv -> inv.getArgument(0));

        // mock DTO i.p.v. aannemen van constructor
        var req = mock(GebruikerUpdateRequest.class);
        when(req.getNaam()).thenReturn("Nieuw");
        when(req.getEmail()).thenReturn("nieuw@example.com");
        when(req.getRol()).thenReturn(Rol.ADMIN);

        var updated = service.updateGebruiker(id, req);

        assertThat(updated.getNaam()).isEqualTo("Nieuw");
        assertThat(updated.getEmail()).isEqualTo("nieuw@example.com");
        assertThat(updated.getRol()).isEqualTo(Rol.ADMIN);

        verify(gebruikerRepository).findById(id);
        verify(gebruikerRepository).existsByEmailIgnoreCaseAndIdNot("nieuw@example.com", id);
        verify(gebruikerRepository).save(any(Gebruiker.class));
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 6
    @Test
    @DisplayName("updateGebruiker: e-mailconflict (andere gebruiker) → 409/Conflict")
    void updateGebruiker_emailConflict_409() {
        Long id = 8L;
        when(gebruikerRepository.findById(id)).thenReturn(Optional.of(mkGebruiker(id, "Tom", "tom@old.nl", Rol.ADMIN)));
        when(gebruikerRepository.existsByEmailIgnoreCaseAndIdNot("bezeta@x.nl", id)).thenReturn(true);

        var req = mock(GebruikerUpdateRequest.class);
        when(req.getNaam()).thenReturn("Tom");
        when(req.getEmail()).thenReturn("bezeta@x.nl");
        when(req.getRol()).thenReturn(Rol.ADMIN);

        assertThatThrownBy(() -> service.updateGebruiker(id, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepository).findById(id);
        verify(gebruikerRepository).existsByEmailIgnoreCaseAndIdNot("bezeta@x.nl", id);
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 7
    @Test
    @DisplayName("deleteGebruiker: OK → deleteById aangeroepen")
    void deleteGebruiker_ok() {
        Long id = 9L;
        when(gebruikerRepository.existsById(id)).thenReturn(true);

        service.deleteGebruiker(id);

        verify(gebruikerRepository).existsById(id);
        verify(gebruikerRepository).deleteById(id);
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }

    // 8
    @Test
    @DisplayName("deleteGebruiker: niet gevonden → 404/Not Found")
    void deleteGebruiker_notFound_404() {
        Long id = 10L;
        when(gebruikerRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteGebruiker(id))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepository).existsById(id);
        verifyNoMoreInteractions(gebruikerRepository, medicatieRepository);
    }
}