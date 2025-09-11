package com.thomas.medicatieinnameapp.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ToedieningCreateRequest {
    // optioneel; als null → nu
    private LocalDateTime tijdstip;

    @Positive
    private Integer hoeveelheid;

    @Size(max = 500)
    private String opmerking;

    private Long schemaId;

    public LocalDateTime getTijdstip() { return tijdstip; }
    public void setTijdstip(LocalDateTime tijdstip) { this.tijdstip = tijdstip; }
    public Integer getHoeveelheid() { return hoeveelheid; }
    public void setHoeveelheid(Integer hoeveelheid) { this.hoeveelheid = hoeveelheid; }
    public String getOpmerking() { return opmerking; }
    public void setOpmerking(String opmerking) { this.opmerking = opmerking; }
    public Long getSchemaId() { return schemaId; }
    public void setSchemaId(Long schemaId) { this.schemaId = schemaId; }
}
