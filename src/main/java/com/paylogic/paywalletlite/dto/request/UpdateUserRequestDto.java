package com.paylogic.paywalletlite.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UpdateUserRequestDto {

    private String firstName;
    private String lastName;

    @Email
    private String email;

    @Size(min = 4, max = 4)
    private String pin;

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}