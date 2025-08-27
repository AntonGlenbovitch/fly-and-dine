package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for synchronizing local data with the Core Reservation System (CRS).
 * Provides methods for bidirectional synchronization with conflict resolution.
 */
public interface SyncService {
    
    /**
     * Synchronizes all pending orders with the CRS.
     * This is the main entry point for synchronization operations.
     * 
     * @return SyncResult containing details of the synchronization operation
     */
    SyncResult synchronizeAll();
    
    /**
     * Synchronizes a specific order with the CRS.
     * 
     * @param orderId the ID of the order to synchronize
     * @return SyncResult containing details of the synchronization operation
     */
    SyncResult synchronizeOrder(UUID orderId);
    
    /**
     * Synchronizes a batch of orders with the CRS.
     * More efficient than individual synchronization for multiple orders.
     * 
     * @param orderIds list of order IDs to synchronize
     * @return SyncResult containing details of the synchronization operation
     */
    SyncResult synchronizeBatch(List<UUID> orderIds);
    
    /**
     * Pulls updates from the CRS for orders that may have been modified externally.
     * This includes status updates, confirmations, and cancellations.
     * 
     * @return SyncResult containing details of the pull operation
     */
    SyncResult pullUpdatesFromCRS();
    
    /**
     * Pushes local changes to the CRS.
     * This includes new orders, modifications, and cancellations.
     * 
     * @return SyncResult containing details of the push operation
     */
    SyncResult pushChangesToCRS();
    
    /**
     * Checks if synchronization is currently possible.
     * This includes network connectivity and CRS availability checks.
     * 
     * @return true if synchronization is possible, false otherwise
     */
    boolean isSyncAvailable();
    
    /**
     * Gets the last successful synchronization timestamp.
     * 
     * @return timestamp of last successful sync, or null if never synced
     */
    java.time.LocalDateTime getLastSyncTime();
    
    /**
     * Gets the number of orders pending synchronization.
     * 
     * @return count of orders that need to be synced
     */
    int getPendingSyncCount();
    
    /**
     * Validates the integrity of local data before synchronization.
     * 
     * @return ValidationResult containing any data integrity issues
     */
    ValidationResult validateLocalData();
    
    /**
     * Resolves conflicts for orders that have been modified both locally and remotely.
     * 
     * @param conflictedOrders list of orders with conflicts
     * @param strategy the conflict resolution strategy to use
     * @return ConflictResolutionResult containing the resolution outcomes
     */
    ConflictResolutionResult resolveConflicts(List<Order> conflictedOrders, ConflictResolutionStrategy strategy);
    
    /**
     * Registers a listener for synchronization events.
     * 
     * @param listener the listener to register
     */
    void addSyncListener(SyncListener listener);
    
    /**
     * Unregisters a synchronization event listener.
     * 
     * @param listener the listener to unregister
     */
    void removeSyncListener(SyncListener listener);
}

