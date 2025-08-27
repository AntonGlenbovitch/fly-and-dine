package com.airline.ordering.repository;

import com.airline.ordering.domain.Order;
import com.airline.ordering.domain.OrderStatus;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Order entities.
 */
public interface OrderRepository extends Repository<Order, UUID> {
    
    /**
     * Finds all orders for a given passenger ID.
     * @param passengerId The ID of the passenger.
     * @return A list of orders for the specified passenger.
     */
    List<Order> findByPassengerId(UUID passengerId);
    
    /**
     * Finds all orders for a given seat number.
     * @param seatNumber The seat number.
     * @return A list of orders for the specified seat.
     */
    List<Order> findBySeatNumber(String seatNumber);
    
    /**
     * Finds all orders with a specific status.
     * @param status The status of the orders.
     * @return A list of orders with the specified status.
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Finds all orders that need synchronization with the CRS.
     * @return A list of orders requiring synchronization.
     */
    List<Order> findOrdersRequiringSync();
}

