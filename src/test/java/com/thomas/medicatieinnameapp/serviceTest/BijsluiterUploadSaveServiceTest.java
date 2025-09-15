package com.thomas.medicatieinnameapp.serviceTest;

import com.thomas.medicatieinnameapp.service.MedicatieService;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.model.MedicatieBijsluiter;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieBijsluiterRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BijsluiterUploadSaveServiceTest {

    @Mock MedicatieRepository medicatieRepository;
    @Mock GebruikerRepository gebruikerRepository;
    @Mock MedicatieBijsluiterRepository bijsluiterRepository;

    @InjectMocks MedicatieService service;

    @Test
    @DisplayName("saveBijsluiterFoto: maakt nieuwe bijsluiter aan wanneer niet bestaand")
    void saveBijsluiterFoto_createsNew_ok() {
        Long id = 1L;
        byte[] data = "PDF_BYTES".getBytes();

        Medicatie m = new Medicatie();
        m.setId(id);

        when(medicatieRepository.findById(id)).thenReturn(Optional.of(m));
        when(bijsluiterRepository.findById(id)).thenReturn(Optional.empty());
        when(bijsluiterRepository.save(any(MedicatieBijsluiter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.saveBijsluiterFoto(id, data);

        ArgumentCaptor<MedicatieBijsluiter> cap = ArgumentCaptor.forClass(MedicatieBijsluiter.class);
        verify(bijsluiterRepository).save(cap.capture());

        MedicatieBijsluiter saved = cap.getValue();
        assertThat(saved.getMedicatie()).isSameAs(m);
        assertThat(saved.getData()).isEqualTo(data);
        assertThat(saved.getSizeBytes()).isEqualTo((long) data.length);
        assertThat(saved.getContentType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("saveBijsluiterFoto: overschrijft bestaande bijsluiter")
    void saveBijsluiterFoto_updatesExisting_ok() {
        Long id = 2L;
        byte[] newData = "NIEUWE_BYTES".getBytes();

        Medicatie m = new Medicatie();
        m.setId(id);

        MedicatieBijsluiter bestaand = new MedicatieBijsluiter();
        bestaand.setMedicatie(m);
        bestaand.setData("OUDE_BYTES".getBytes());

        when(medicatieRepository.findById(id)).thenReturn(Optional.of(m));
        when(bijsluiterRepository.findById(id)).thenReturn(Optional.of(bestaand));
        when(bijsluiterRepository.save(any(MedicatieBijsluiter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.saveBijsluiterFoto(id, newData);

        ArgumentCaptor<MedicatieBijsluiter> cap = ArgumentCaptor.forClass(MedicatieBijsluiter.class);
        verify(bijsluiterRepository).save(cap.capture());

        MedicatieBijsluiter saved = cap.getValue();
        assertThat(saved).isSameAs(bestaand);
        assertThat(saved.getData()).isEqualTo(newData);
        assertThat(saved.getSizeBytes()).isEqualTo((long) newData.length);
        assertThat(saved.getContentType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("saveBijsluiterFoto: lege bytes → 400 BAD_REQUEST")
    void saveBijsluiterFoto_empty_badRequest() {
        assertThatThrownBy(() -> service.saveBijsluiterFoto(10L, new byte[0]))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Leeg bestand");
        verifyNoInteractions(medicatieRepository, bijsluiterRepository);
    }

    @Test
    @DisplayName("saveBijsluiterFoto: >5MB → 413 PAYLOAD_TOO_LARGE")
    void saveBijsluiterFoto_tooLarge_payloadTooLarge() {
        byte[] big = new byte[5 * 1024 * 1024 + 1]; // 5MB + 1 byte
        assertThatThrownBy(() -> service.saveBijsluiterFoto(11L, big))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("te groot");
        verifyNoInteractions(medicatieRepository, bijsluiterRepository);
    }

    @Test
    @DisplayName("getBijsluiterFoto: ok → bytes terug")
    void getBijsluiterFoto_ok() {
        Long id = 3L;
        byte[] payload = "HELLO".getBytes();

        MedicatieBijsluiter b = new MedicatieBijsluiter();
        b.setData(payload);

        when(bijsluiterRepository.findById(id)).thenReturn(Optional.of(b));

        byte[] result = service.getBijsluiterFoto(id);
        assertThat(result).isEqualTo(payload);
    }

    @Test
    @DisplayName("getBijsluiterFoto: geen record → 404 NOT_FOUND")
    void getBijsluiterFoto_notFound_404() {
        when(bijsluiterRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBijsluiterFoto(404L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Geen bijsluiter");
    }

    @Test
    @DisplayName("deleteBijsluiterFoto: ok → deleteById aangeroepen")
    void deleteBijsluiterFoto_ok() {
        Long id = 12L;
        when(bijsluiterRepository.existsById(id)).thenReturn(true);

        service.deleteBijsluiterFoto(id);

        verify(bijsluiterRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteBijsluiterFoto: niet gevonden → 404 NOT_FOUND")
    void deleteBijsluiterFoto_notFound_404() {
        Long id = 13L;
        when(bijsluiterRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteBijsluiterFoto(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Geen bijsluiter");
        verify(bijsluiterRepository, never()).deleteById(any());
    }
}
