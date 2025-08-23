package com.airline.ordering.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a passenger in the inflight ordering system.
 * Contains PII that must be handled securely.
 */
public class Passenger {
    
    @NotNull
    private UUID passengerId;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;
    
    private String phoneNumber;
    
    @NotBlank
    private String seatNumber;
    
    @NotNull
    private PassengerType type;
    
    private String specialRequirements;
    
    @NotNull
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Passenger() {
        this.passengerId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Passenger(String firstName, String lastName, String email, 
                    String seatNumber, PassengerType type) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.seatNumber = seatNumber;
        this.type = type;
    }
    
    // Getters and Setters
    public UUID getPassengerId() {
        return passengerId;
    }
    
    public void setPassengerId(UUID passengerId) {
        this.passengerId = passengerId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public PassengerType getType() {
        return type;
    }
    
    public void setType(PassengerType type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSpecialRequirements() {
        return specialRequirements;
    }
    
    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean hasSpecialRequirements() {
        return specialRequirements != null && !specialRequirements.trim().isEmpty();
    }
    
    public boolean isVip() {
        return type == PassengerType.FIRST_CLASS || type == PassengerType.BUSINESS;
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(passengerId, passenger.passengerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(passengerId);
    }
    
    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", type=" + type +
                '}';
    }
}

