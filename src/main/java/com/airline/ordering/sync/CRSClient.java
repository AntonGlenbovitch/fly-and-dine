package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for communicating with the Core Reservation System (CRS).
 * Provides methods for pushing and pulling order data to/from the CRS.
 */
public interface CRSClient {
    
    /**
     * Pushes a single order to the CRS.
     * 
     * @param order the order to push
     * @return CRSResponse containing the result of the operation
     */
    CRSResponse pushOrder(Order order);
    
    /**
     * Pushes multiple orders to the CRS in a batch operation.
     * 
     * @param orders the list of orders to push
     * @return CRSResponse containing the result of the batch operation
     */
    CRSResponse pushOrders(List<Order> orders);
    
    /**
     * Pulls a single order from the CRS by ID.
     * 
     * @param orderId the ID of the order to retrieve
     * @return CRSResponse containing the order data or error information
     */
    CRSResponse pullOrder(UUID orderId);
    
    /**
     * Pulls multiple orders from the CRS by their IDs.
     * 
     * @param orderIds the list of order IDs to retrieve
     * @return CRSResponse containing the order data or error information
     */
    CRSResponse pullOrders(List<UUID> orderIds);
    
    /**
     * Pulls all orders that have been updated since the specified timestamp.
     * 
     * @param since the timestamp to check for updates since
     * @return CRSResponse containing the updated orders
     */
    CRSResponse pullUpdatedOrdersSince(java.time.LocalDateTime since);
    
    /**
     * Pulls orders for a specific passenger.
     * 
     * @param passengerId the ID of the passenger
     * @return CRSResponse containing the passenger's orders
     */
    CRSResponse pullOrdersForPassenger(UUID passengerId);
    
    /**
     * Pulls orders for a specific seat.
     * 
     * @param seatNumber the seat number
     * @return CRSResponse containing the seat's orders
     */
    CRSResponse pullOrdersForSeat(String seatNumber);
    
    /**
     * Deletes an order from the CRS.
     * 
     * @param orderId the ID of the order to delete
     * @return CRSResponse containing the result of the deletion
     */
    CRSResponse deleteOrder(UUID orderId);
    
    /**
     * Checks if the CRS is currently available and reachable.
     * 
     * @return true if the CRS is available, false otherwise
     */
    boolean isAvailable();
    
    /**
     * Performs a health check on the CRS connection.
     * 
     * @return CRSHealthStatus containing detailed health information
     */
    CRSHealthStatus checkHealth();
    
    /**
     * Authenticates with the CRS using the provided credentials.
     * 
     * @param credentials the authentication credentials
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(CRSCredentials credentials);
    
    /**
     * Gets the current authentication status.
     * 
     * @return true if currently authenticated, false otherwise
     */
    boolean isAuthenticated();
    
    /**
     * Refreshes the authentication token if needed.
     * 
     * @return true if refresh is successful, false otherwise
     */
    boolean refreshAuthentication();
    
    /**
     * Gets the maximum batch size supported by the CRS.
     * 
     * @return the maximum number of orders that can be sent in a single batch
     */
    int getMaxBatchSize();
    
    /**
     * Gets the rate limit information for the CRS API.
     * 
     * @return CRSRateLimit containing rate limit details
     */
    CRSRateLimit getRateLimit();
    
    /**
     * Validates an order against CRS business rules before pushing.
     * 
     * @param order the order to validate
     * @return CRSValidationResult containing validation results
     */
    CRSValidationResult validateOrder(Order order);
    
    /**
     * Gets the server timestamp from the CRS for synchronization purposes.
     * 
     * @return the current server timestamp
     */
    java.time.LocalDateTime getServerTimestamp();
    
    /**
     * Registers for real-time notifications from the CRS.
     * 
     * @param listener the listener to receive notifications
     * @return true if registration is successful, false otherwise
     */
    boolean registerForNotifications(CRSNotificationListener listener);
    
    /**
     * Unregisters from real-time notifications.
     * 
     * @param listener the listener to unregister
     * @return true if unregistration is successful, false otherwise
     */
    boolean unregisterFromNotifications(CRSNotificationListener listener);
}

