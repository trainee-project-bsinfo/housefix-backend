package eu.bsinfo.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.web.LocalDateSerializer;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Reading implements IReading {
    private UUID id;
    private KindOfMeter kindOfMeter;
    private LocalDate dateOfReading;
    private Customer customer;
    private String comment;
    private double meterCount;
    private String meterId;
    private Boolean substitute;

    @JsonCreator
    public Reading(@JsonProperty("id") UUID id,
                   @JsonProperty("kindOfMeter") KindOfMeter kindOfMeter,
                   @JsonProperty("dateOfReading") @JsonSerialize(using = LocalDateSerializer.class) LocalDate dateOfReading,
                   @JsonProperty("customer") Customer customer,
                   @JsonProperty("comment") String comment,
                   @JsonProperty("meterCount") Double meterCount,
                   @JsonProperty("meterId") String meterId,
                   @JsonProperty("substitute") Boolean substitute) {
        if (kindOfMeter == null || dateOfReading == null || comment == null || meterCount == null || meterId == null || substitute == null) {
            throw new IllegalArgumentException("Reading is missing required fields");
        }
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.kindOfMeter = kindOfMeter;
        this.dateOfReading = dateOfReading;
        this.customer = customer;
        this.comment = comment;
        this.meterCount = meterCount;
        this.meterId = meterId;
        this.substitute = substitute;
    }

    public Reading(KindOfMeter kindOfMeter, LocalDate dateOfReading, Customer customer, String comment, double meterCount, String meterId, Boolean substitute) {
        this.id = UUID.randomUUID();
        this.kindOfMeter = kindOfMeter;
        this.dateOfReading = dateOfReading;
        this.customer = customer;
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
    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    public Customer getCustomer() {
        return customer;
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
