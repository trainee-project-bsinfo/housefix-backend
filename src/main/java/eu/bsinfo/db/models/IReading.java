package eu.bsinfo.db.models;


import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.web.dto.Customer;

import java.time.LocalDate;

public interface IReading extends IID {
    void setComment(String comment);
    void setCustomer(Customer customer);
    void setDateOfReading(LocalDate dateOfReading);
    void setKindOfMeter(KindOfMeter kindOfMeter);
    void setMeterCount(Double meterCount);
    void setMeterId(String meterID);
    void setSubstitute(Boolean substitute);

    String getComment();
    Customer getCustomer();
    LocalDate getDateOfReading();
    KindOfMeter getKindOfMeter();
    Double getMeterCount();
    String getMeterId();
    Boolean getSubstitute();
    String printDateOfReading();

}
