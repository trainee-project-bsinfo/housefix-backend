package eu.bsinfo.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.bsinfo.db.models.Customer;

import java.util.List;

public class CustomersDto {
    private List<Customer> customers;

    @JsonCreator
    public CustomersDto(@JsonProperty("customers")List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
