package eu.bsinfo.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.db.models.IReading;

import java.time.LocalDate;
import java.util.Objects;
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

    @JsonCreator
    public Reading(@JsonProperty("id") UUID id,
                   @JsonProperty("kindOfMeter") KindOfMeter kindOfMeter,
                   @JsonProperty("dateOfReading") LocalDate dateOfReading,
                   @JsonProperty("customerId") UUID customerId,
                   @JsonProperty("comment") String comment,
                   @JsonProperty("meterCount") double meterCount,
                   @JsonProperty("meterId") String meterId,
                   @JsonProperty("substitute") Boolean substitute) {
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
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
