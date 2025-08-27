package com.airline.ordering.repository.impl;

import com.airline.ordering.domain.Passenger;
import com.airline.ordering.domain.PassengerType;
import com.airline.ordering.repository.PassengerRepository;
import com.airline.ordering.repository.SQLiteConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PassengerRepositoryImpl implements PassengerRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PassengerRepositoryImpl.class);
    
    @Override
    public Passenger save(Passenger passenger) {
        String sql = "INSERT INTO passengers(passenger_id, first_name, last_name, email, phone_number, seat_number, type, special_requirements, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?,?)"
                     + " ON CONFLICT(passenger_id) DO UPDATE SET first_name=?, last_name=?, email=?, phone_number=?, seat_number=?, type=?, special_requirements=?, updated_at=?";
        
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            boolean isNew = findById(passenger.getPassengerId()).isEmpty();
            
            pstmt.setString(1, passenger.getPassengerId().toString());
            pstmt.setString(2, passenger.getFirstName());
            pstmt.setString(3, passenger.getLastName());
            pstmt.setString(4, passenger.getEmail());
            pstmt.setString(5, passenger.getPhoneNumber());
            pstmt.setString(6, passenger.getSeatNumber());
            pstmt.setString(7, passenger.getType().name());
            pstmt.setString(8, passenger.getSpecialRequirements());
            pstmt.setString(9, passenger.getCreatedAt().toString());
            pstmt.setString(10, LocalDateTime.now().toString()); // Always update updated_at
            
            // For ON CONFLICT UPDATE part
            pstmt.setString(11, passenger.getFirstName());
            pstmt.setString(12, passenger.getLastName());
            pstmt.setString(13, passenger.getEmail());
            pstmt.setString(14, passenger.getPhoneNumber());
            pstmt.setString(15, passenger.getSeatNumber());
            pstmt.setString(16, passenger.getType().name());
            pstmt.setString(17, passenger.getSpecialRequirements());
            pstmt.setString(18, LocalDateTime.now().toString());
            
            pstmt.executeUpdate();
            logger.info("Passenger saved: {}", passenger.getPassengerId());
            return passenger;
        } catch (SQLException e) {
            logger.error("Error saving passenger {}: {}", passenger.getPassengerId(), e.getMessage());
            throw new RuntimeException("Error saving passenger", e);
        }
    }
    
    @Override
    public Optional<Passenger> findById(UUID id) {
        String sql = "SELECT * FROM passengers WHERE passenger_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToPassenger(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding passenger by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error finding passenger by ID", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Passenger> findAll() {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                passengers.add(mapRowToPassenger(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all passengers: {}", e.getMessage());
            throw new RuntimeException("Error finding all passengers", e);
        }
        return passengers;
    }
    
    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM passengers WHERE passenger_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
            logger.info("Passenger deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting passenger by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting passenger by ID", e);
        }
    }
    
    @Override
    public void delete(Passenger entity) {
        deleteById(entity.getPassengerId());
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM passengers";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting passengers: {}", e.getMessage());
            throw new RuntimeException("Error counting passengers", e);
        }
        return 0;
    }
    
    @Override
    public Optional<Passenger> findBySeatNumber(String seatNumber) {
        String sql = "SELECT * FROM passengers WHERE seat_number = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, seatNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToPassenger(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding passenger by seat number {}: {}", seatNumber, e.getMessage());
            throw new RuntimeException("Error finding passenger by seat number", e);
        }
        return Optional.empty();
    }
    
    private Passenger mapRowToPassenger(ResultSet rs) throws SQLException {
        Passenger passenger = new Passenger();
        passenger.setPassengerId(UUID.fromString(rs.getString("passenger_id")));
        passenger.setFirstName(rs.getString("first_name"));
        passenger.setLastName(rs.getString("last_name"));
        passenger.setEmail(rs.getString("email"));
        passenger.setPhoneNumber(rs.getString("phone_number"));
        passenger.setSeatNumber(rs.getString("seat_number"));
        passenger.setType(PassengerType.valueOf(rs.getString("type")));
        passenger.setSpecialRequirements(rs.getString("special_requirements"));
        passenger.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
        passenger.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at")));
        return passenger;
    }
}

