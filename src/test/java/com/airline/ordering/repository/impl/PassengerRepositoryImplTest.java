package com.airline.ordering.repository.impl;

import com.airline.ordering.domain.Passenger;
import com.airline.ordering.domain.PassengerType;
import com.airline.ordering.repository.SQLiteConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PassengerRepositoryImplTest {
    
    private PassengerRepositoryImpl passengerRepository;
    private static final String DB_FILE = "inflight_ordering.db";
    
    @BeforeEach
    void setUp() {
        // Ensure a clean database for each test
        File dbFile = new File(DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        SQLiteConnection.initializeDatabase();
        passengerRepository = new PassengerRepositoryImpl();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up database file after each test
        File dbFile = new File(DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }
    
    @Test
    void testSaveAndFindById() {
        Passenger passenger = new Passenger("John", "Doe", "john.doe@example.com", "1A", PassengerType.ECONOMY);
        Passenger savedPassenger = passengerRepository.save(passenger);
        
        assertNotNull(savedPassenger);
        assertEquals(passenger.getPassengerId(), savedPassenger.getPassengerId());
        
        Optional<Passenger> foundPassenger = passengerRepository.findById(passenger.getPassengerId());
        assertTrue(foundPassenger.isPresent());
        assertEquals(passenger.getFirstName(), foundPassenger.get().getFirstName());
        assertEquals(passenger.getSeatNumber(), foundPassenger.get().getSeatNumber());
    }
    
    @Test
    void testUpdatePassenger() {
        Passenger passenger = new Passenger("Jane", "Smith", "jane.smith@example.com", "2B", PassengerType.BUSINESS);
        passengerRepository.save(passenger);
        
        passenger.setFirstName("Janet");
        passenger.setEmail("janet.smith@example.com");
        passengerRepository.save(passenger);
        
        Optional<Passenger> updatedPassenger = passengerRepository.findById(passenger.getPassengerId());
        assertTrue(updatedPassenger.isPresent());
        assertEquals("Janet", updatedPassenger.get().getFirstName());
        assertEquals("janet.smith@example.com", updatedPassenger.get().getEmail());
    }
    
    @Test
    void testFindAll() {
        Passenger p1 = new Passenger("Alice", "Brown", "alice@example.com", "3C", PassengerType.FIRST_CLASS);
        Passenger p2 = new Passenger("Bob", "White", "bob@example.com", "4D", PassengerType.CREW);
        passengerRepository.save(p1);
        passengerRepository.save(p2);
        
        List<Passenger> passengers = passengerRepository.findAll();
        assertEquals(2, passengers.size());
        assertTrue(passengers.stream().anyMatch(p -> p.getFirstName().equals("Alice")));
        assertTrue(passengers.stream().anyMatch(p -> p.getFirstName().equals("Bob")));
    }
    
    @Test
    void testDeleteById() {
        Passenger passenger = new Passenger("Charlie", "Green", "charlie@example.com", "5E", PassengerType.PREMIUM_ECONOMY);
        passengerRepository.save(passenger);
        
        passengerRepository.deleteById(passenger.getPassengerId());
        
        Optional<Passenger> foundPassenger = passengerRepository.findById(passenger.getPassengerId());
        assertFalse(foundPassenger.isPresent());
    }
    
    @Test
    void testCount() {
        assertEquals(0, passengerRepository.count());
        
        passengerRepository.save(new Passenger("David", "Black", "david@example.com", "6F", PassengerType.ECONOMY));
        assertEquals(1, passengerRepository.count());
        
        passengerRepository.save(new Passenger("Eve", "Gray", "eve@example.com", "7G", PassengerType.BUSINESS));
        assertEquals(2, passengerRepository.count());
    }
    
    @Test
    void testFindBySeatNumber() {
        Passenger passenger = new Passenger("Frank", "Blue", "frank@example.com", "8H", PassengerType.CREW);
        passengerRepository.save(passenger);
        
        Optional<Passenger> foundPassenger = passengerRepository.findBySeatNumber("8H");
        assertTrue(foundPassenger.isPresent());
        assertEquals("Frank", foundPassenger.get().getFirstName());
        
        assertFalse(passengerRepository.findBySeatNumber("9Z").isPresent());
    }
    
    @Test
    void testSaveWithExistingIdUpdates() {
        Passenger passenger = new Passenger("Original", "Name", "original@example.com", "10J", PassengerType.ECONOMY);
        passengerRepository.save(passenger);
        
        passenger.setFirstName("Updated");
        passenger.setEmail("updated@example.com");
        passengerRepository.save(passenger);
        
        Optional<Passenger> retrieved = passengerRepository.findById(passenger.getPassengerId());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated", retrieved.get().getFirstName());
        assertEquals("updated@example.com", retrieved.get().getEmail());
        assertEquals(1, passengerRepository.count()); // Should still be only one record
    }
}

