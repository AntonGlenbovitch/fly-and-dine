package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.util.UUID;

/**
 * Interface for receiving real-time notifications from the Core Reservation System (CRS).
 */
public interface CRSNotificationListener {
    
    /**
     * Called when an order is confirmed by the CRS.
     * 
     * @param orderId the ID of the confirmed order
     * @param confirmationNumber the CRS confirmation number
     */
    void onOrderConfirmed(UUID orderId, String confirmationNumber);
    
    /**
     * Called when an order is cancelled by the CRS.
     * 
     * @param orderId the ID of the cancelled order
     * @param reason the cancellation reason
     */
    void onOrderCancelled(UUID orderId, String reason);
    
    /**
     * Called when an order status is updated by the CRS.
     * 
     * @param orderId the ID of the updated order
     * @param newStatus the new order status
     * @param previousStatus the previous order status
     */
    void onOrderStatusUpdated(UUID orderId, String newStatus, String previousStatus);
    
    /**
     * Called when an order is modified by the CRS or another system.
     * 
     * @param orderId the ID of the modified order
     * @param updatedOrder the updated order data
     */
    void onOrderModified(UUID orderId, Order updatedOrder);
    
    /**
     * Called when payment for an order is processed.
     * 
     * @param orderId the ID of the order
     * @param paymentStatus the payment status (e.g., "PAID", "FAILED", "PENDING")
     * @param transactionId the payment transaction ID
     */
    void onPaymentProcessed(UUID orderId, String paymentStatus, String transactionId);
    
    /**
     * Called when an order is ready for delivery.
     * 
     * @param orderId the ID of the order
     * @param estimatedDeliveryTime the estimated delivery time
     */
    void onOrderReadyForDelivery(UUID orderId, java.time.LocalDateTime estimatedDeliveryTime);
    
    /**
     * Called when an order is delivered.
     * 
     * @param orderId the ID of the delivered order
     * @param deliveryTime the actual delivery time
     */
    void onOrderDelivered(UUID orderId, java.time.LocalDateTime deliveryTime);
    
    /**
     * Called when there's an issue with an order that requires attention.
     * 
     * @param orderId the ID of the order
     * @param issueType the type of issue
     * @param description the issue description
     * @param severity the severity level
     */
    void onOrderIssue(UUID orderId, String issueType, String description, String severity);
    
    /**
     * Called when inventory levels change for menu items.
     * 
     * @param menuItemId the ID of the menu item
     * @param newQuantity the new available quantity
     * @param previousQuantity the previous available quantity
     */
    void onInventoryUpdated(UUID menuItemId, int newQuantity, int previousQuantity);
    
    /**
     * Called when a menu item becomes unavailable.
     * 
     * @param menuItemId the ID of the unavailable menu item
     * @param reason the reason for unavailability
     */
    void onMenuItemUnavailable(UUID menuItemId, String reason);
    
    /**
     * Called when the CRS connection status changes.
     * 
     * @param connected true if connected, false if disconnected
     * @param reason the reason for the status change
     */
    void onConnectionStatusChanged(boolean connected, String reason);
    
    /**
     * Called when a general system notification is received.
     * 
     * @param notificationType the type of notification
     * @param message the notification message
     * @param data additional notification data
     */
    void onSystemNotification(String notificationType, String message, java.util.Map<String, Object> data);
    
    /**
     * Called when an error occurs in the notification system.
     * 
     * @param error the error that occurred
     */
    void onNotificationError(Exception error);
}

