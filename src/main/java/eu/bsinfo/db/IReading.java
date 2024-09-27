package eu.bsinfo.db;


import java.time.LocalDate;

public interface IReading extends IID {
    void setComment(String comment);
    void setCustomer(ICustomer customer);
    void setDateOfReading(LocalDate dateOfReading);
    void setKindOfMeter(KindOfMeter  kindOfMeter);
    void setMeterCount(Double meterCount);
    void setMeterId(String meterID);
    void setSubstitute(Boolean substitute);

    String getComment();
    ICustomer getCustomer();
    LocalDate getDateOfReading();
    KindOfMeter getKindOfMeter();
    Double getMeterCount();
    String getMeterId();
    Boolean getSubstitute();
    String printDateOfReading();

}
