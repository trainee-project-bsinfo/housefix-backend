package eu.bsinfo.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.bsinfo.db.models.Customer;
import eu.bsinfo.db.models.Reading;

import java.util.List;

public class CustomerWithReadingsDto {
    private Customer customer;
    private List<Reading> readings;

    @JsonCreator
    public CustomerWithReadingsDto(@JsonProperty("customer") Customer customer,
                                   @JsonProperty("readings") List<Reading> readings)
    {
        this.customer = customer;
        this.readings = readings.stream().peek(r -> r.setCustomer(null)).toList();
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public List<Reading> getReadings() {
        return readings;
    }
    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }
}
