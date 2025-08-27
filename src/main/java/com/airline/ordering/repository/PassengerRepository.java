package com.airline.ordering.repository;

import com.airline.ordering.domain.Passenger;
import java.util.UUID;

/**
 * Repository interface for Passenger entities.
 */
public interface PassengerRepository extends Repository<Passenger, UUID> {
    
    /**
     * Finds a passenger by their seat number.
     * @param seatNumber The seat number of the passenger.
     * @return An Optional containing the passenger if found, empty otherwise.
     */
    java.util.Optional<Passenger> findBySeatNumber(String seatNumber);
}

