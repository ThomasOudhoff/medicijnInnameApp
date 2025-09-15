package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicatie_bijsluiter")
public class MedicatieBijsluiter {

    @Id
    @Column(name = "medicatie_id")
    private Long medicatieId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "medicatie_id")
    private Medicatie medicatie;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data")
    private byte[] data;

    @Column(length = 500)
    private String url;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    public MedicatieBijsluiter() {
    }

    // --- Getters & Setters ---
    public Long getMedicatieId() {
        return medicatieId;
    }

    public void setMedicatieId(Long medicatieId) {
        this.medicatieId = medicatieId;
    }

    public Medicatie getMedicatie() {
        return medicatie;
    }

    public void setMedicatie(Medicatie medicatie) {
        this.medicatie = medicatie;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
}
