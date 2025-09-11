package com.thomas.medicatieinnameapp.dto;

import java.time.LocalDateTime;

public class ToedieningResponse {
    private Long id;
    private Long gebruikerId;
    private Long medicatieId;
    private Long schemaId;      // kan null zijn
    private LocalDateTime tijdstip;
    private Integer hoeveelheid;
    private String opmerking;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGebruikerId() { return gebruikerId; }
    public void setGebruikerId(Long gebruikerId) { this.gebruikerId = gebruikerId; }
    public Long getMedicatieId() { return medicatieId; }
    public void setMedicatieId(Long medicatieId) { this.medicatieId = medicatieId; }
    public Long getSchemaId() { return schemaId; }
    public void setSchemaId(Long schemaId) { this.schemaId = schemaId; }
    public LocalDateTime getTijdstip() { return tijdstip; }
    public void setTijdstip(LocalDateTime tijdstip) { this.tijdstip = tijdstip; }
    public Integer getHoeveelheid() { return hoeveelheid; }
    public void setHoeveelheid(Integer hoeveelheid) { this.hoeveelheid = hoeveelheid; }
    public String getOpmerking() { return opmerking; }
    public void setOpmerking(String opmerking) { this.opmerking = opmerking; }
}
