package com.thomas.medicatieinnameapp.controller;
import org.springframework.web.bind.annotation.*;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.service.MedicatieService;

import java.util.List;

@RestController
@RequestMapping("/api/medicatie")
public class MedicatieController {
    private final MedicatieService medicatieService;


    public MedicatieController(MedicatieService medicatieService) {
        this.medicatieService = medicatieService;
    }

    @GetMapping
    public List<Medicatie> getAllMedicatie() {
        return medicatieService.getAllMedicatie();
    }
    @PostMapping
    public Medicatie saveMedicatie(@RequestBody Medicatie medicatie) {
        return medicatieService.saveMedicatie(medicatie);
    }

}
