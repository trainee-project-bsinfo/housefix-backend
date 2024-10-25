package eu.bsinfo.db.dto;

import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.db.models.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading implements IReading {
    private UUID id;
    private KindOfMeter kindOfMeter;
    private LocalDate dateOfReading;
    private UUID customerId;
    private String comment;
    private double meterCount;
    private String meterId;
    private Boolean substitute;

    public Reading(UUID id, KindOfMeter kindOfMeter, LocalDate dateOfReading, UUID customerId, String comment, double meterCount, String meterId, Boolean substitute) {
        this.id = id;
        this.kindOfMeter = kindOfMeter;
        this.dateOfReading = dateOfReading;
        this.customerId = customerId;
        this.comment = comment;
        this.meterCount = meterCount;
        this.meterId = meterId;
        this.substitute = substitute;
    }

    public Reading(KindOfMeter kindOfMeter, LocalDate dateOfReading, UUID customerId, String comment, double meterCount, String meterId, Boolean substitute) {
        this.id = UUID.randomUUID();
        this.kindOfMeter = kindOfMeter;
        this.dateOfReading = dateOfReading;
        this.customerId = customerId;
        this.comment = comment;
        this.meterCount = meterCount;
        this.meterId = meterId;
        this.substitute = substitute;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    @Override
    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    @Override
    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    }

    @Override
    public void setMeterId(String meterID) {
        this.meterId = meterID;
    }

    @Override
    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public UUID getCustomerId() {
        return customerId;
    }

    @Override
    public LocalDate getDateOfReading() {
        return dateOfReading;
    }

    @Override
    public KindOfMeter getKindOfMeter() {
        return kindOfMeter;
    }

    @Override
    public Double getMeterCount() {
        return meterCount;
    }

    @Override
    public String getMeterId() {
        return meterId;
    }

    @Override
    public Boolean getSubstitute() {
        return substitute;
    }

    @Override
    public String printDateOfReading() {
        return dateOfReading.toString();
    }

    @Override
    public UUID getid() {
        return id;
    }

    @Override
    public void setid(UUID id) {
        this.id = id;
    }
}
