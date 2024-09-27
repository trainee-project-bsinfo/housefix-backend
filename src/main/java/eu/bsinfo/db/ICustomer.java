package eu.bsinfo.db;

import java.time.LocalDate;

public interface ICustomer {
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setBirtDate(LocalDate birtDate);
    void setGender(Gender gender);
    String  getFirstName();
    String  getLastName();
    LocalDate  getBirtDate();
    Gender getGender();
}
