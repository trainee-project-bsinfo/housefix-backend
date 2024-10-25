package eu.bsinfo.db.models;


import eu.bsinfo.db.enums.KindOfMeter;

import java.time.LocalDate;
import java.util.UUID;

public interface IReading extends IID {
    void setComment(String comment);
    void setCustomerId(UUID customerId);
    void setDateOfReading(LocalDate dateOfReading);
    void setKindOfMeter(KindOfMeter kindOfMeter);
    void setMeterCount(Double meterCount);
    void setMeterId(String meterID);
    void setSubstitute(Boolean substitute);

    String getComment();
    UUID getCustomerId();
    LocalDate getDateOfReading();
    KindOfMeter getKindOfMeter();
    Double getMeterCount();
    String getMeterId();
    Boolean getSubstitute();
    String printDateOfReading();

}
