package com.airline.ordering.service;

import com.airline.ordering.domain.Order;
import com.airline.ordering.domain.OrderItem;
import com.airline.ordering.domain.Passenger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing orders in the inflight ordering system.
 * Provides business logic for order creation, modification, and lifecycle management.
 */
public interface OrderService {
    
    /**
     * Creates a new order for a passenger.
     * 
     * @param passenger The passenger placing the order
     * @return The newly created order
     */
    Order createOrder(Passenger passenger);
    
    /**
     * Retrieves an order by its ID.
     * 
     * @param orderId The unique identifier of the order
     * @return Optional containing the order if found, empty otherwise
     */
    Optional<Order> getOrder(UUID orderId);
    
    /**
     * Retrieves all orders for a specific passenger.
     * 
     * @param passengerId The unique identifier of the passenger
     * @return List of orders for the passenger
     */
    List<Order> getOrdersByPassenger(UUID passengerId);
    
    /**
     * Retrieves all orders for a specific seat.
     * 
     * @param seatNumber The seat number
     * @return List of orders for the seat
     */
    List<Order> getOrdersBySeat(String seatNumber);
    
    /**
     * Adds an item to an existing order.
     * 
     * @param orderId The order ID
     * @param menuItemId The menu item ID to add
     * @param quantity The quantity to add
     * @param specialInstructions Optional special instructions
     * @return The updated order
     * @throws IllegalArgumentException if order is not modifiable or item is invalid
     */
    Order addItemToOrder(UUID orderId, UUID menuItemId, int quantity, String specialInstructions);
    
    /**
     * Removes an item from an order.
     * 
     * @param orderId The order ID
     * @param orderItemId The order item ID to remove
     * @return The updated order
     * @throws IllegalArgumentException if order is not modifiable
     */
    Order removeItemFromOrder(UUID orderId, UUID orderItemId);
    
    /**
     * Updates the quantity of an item in an order.
     * 
     * @param orderId The order ID
     * @param orderItemId The order item ID
     * @param newQuantity The new quantity
     * @return The updated order
     * @throws IllegalArgumentException if order is not modifiable or quantity is invalid
     */
    Order updateItemQuantity(UUID orderId, UUID orderItemId, int newQuantity);
    
    /**
     * Substitutes one item for another in an order.
     * 
     * @param orderId The order ID
     * @param orderItemId The order item ID to substitute
     * @param newMenuItemId The new menu item ID
     * @return The updated order
     * @throws IllegalArgumentException if substitution is not allowed
     */
    Order substituteItem(UUID orderId, UUID orderItemId, UUID newMenuItemId);
    
    /**
     * Submits an order for processing.
     * 
     * @param orderId The order ID
     * @return The updated order
     * @throws IllegalArgumentException if order cannot be submitted
     */
    Order submitOrder(UUID orderId);
    
    /**
     * Confirms an order (typically done by crew).
     * 
     * @param orderId The order ID
     * @return The updated order
     * @throws IllegalArgumentException if order cannot be confirmed
     */
    Order confirmOrder(UUID orderId);
    
    /**
     * Cancels an order.
     * 
     * @param orderId The order ID
     * @param reason The cancellation reason
     * @return The updated order
     * @throws IllegalArgumentException if order cannot be cancelled
     */
    Order cancelOrder(UUID orderId, String reason);
    
    /**
     * Validates an order for business rules compliance.
     * 
     * @param order The order to validate
     * @return List of validation errors, empty if valid
     */
    List<String> validateOrder(Order order);
    
    /**
     * Retrieves all orders that need synchronization with CRS.
     * 
     * @return List of orders requiring sync
     */
    List<Order> getOrdersRequiringSync();
    
    /**
     * Marks an order as successfully synced with CRS.
     * 
     * @param orderId The order ID
     * @param crsOrderId The CRS order identifier
     */
    void markOrderAsSynced(UUID orderId, String crsOrderId);
    
    /**
     * Marks an order sync attempt as failed.
     * 
     * @param orderId The order ID
     */
    void markOrderSyncFailed(UUID orderId);
    
    /**
     * Saves an order to the repository.
     * 
     * @param order The order to save
     * @return The saved order
     */
    Order saveOrder(Order order);
}

