package eu.bsinfo.db.dto;

import eu.bsinfo.db.enums.Gender;
import eu.bsinfo.db.models.ICustomer;

import java.time.LocalDate;
import java.util.UUID;

public class Customer implements ICustomer {
    private UUID id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;

    public Customer(UUID id, String firstName, String lastName, Gender gender, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }
    public Customer(String firstName, String lastName, Gender gender, LocalDate birthDate) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public Gender getGender() {
        return gender;
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
