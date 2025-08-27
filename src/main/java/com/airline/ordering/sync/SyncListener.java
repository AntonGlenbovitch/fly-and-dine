package com.airline.ordering.sync;

import java.util.UUID;

/**
 * Interface for listening to synchronization events.
 * Implementations can be registered with the SyncService to receive notifications
 * about synchronization progress and results.
 */
public interface SyncListener {
    
    /**
     * Called when a synchronization operation starts.
     * 
     * @param operation the type of synchronization operation
     * @param totalRecords the total number of records to be synchronized
     */
    void onSyncStarted(SyncOperation operation, int totalRecords);
    
    /**
     * Called when synchronization progress is updated.
     * 
     * @param operation the type of synchronization operation
     * @param processedRecords the number of records processed so far
     * @param totalRecords the total number of records to be synchronized
     */
    void onSyncProgress(SyncOperation operation, int processedRecords, int totalRecords);
    
    /**
     * Called when a single order is successfully synchronized.
     * 
     * @param orderId the ID of the synchronized order
     * @param operation the type of synchronization operation
     */
    void onOrderSynced(UUID orderId, SyncOperation operation);
    
    /**
     * Called when a single order fails to synchronize.
     * 
     * @param orderId the ID of the failed order
     * @param error the synchronization error
     * @param operation the type of synchronization operation
     */
    void onOrderSyncFailed(UUID orderId, SyncError error, SyncOperation operation);
    
    /**
     * Called when a conflict is detected during synchronization.
     * 
     * @param conflictInfo information about the detected conflict
     * @param operation the type of synchronization operation
     */
    void onConflictDetected(ConflictInfo conflictInfo, SyncOperation operation);
    
    /**
     * Called when a conflict is resolved.
     * 
     * @param conflictInfo information about the resolved conflict
     * @param resolution the resolution that was applied
     * @param operation the type of synchronization operation
     */
    void onConflictResolved(ConflictInfo conflictInfo, ConflictResolution resolution, SyncOperation operation);
    
    /**
     * Called when a synchronization operation completes successfully.
     * 
     * @param result the result of the synchronization operation
     */
    void onSyncCompleted(SyncResult result);
    
    /**
     * Called when a synchronization operation fails.
     * 
     * @param result the result of the failed synchronization operation
     */
    void onSyncFailed(SyncResult result);
    
    /**
     * Called when network connectivity changes.
     * 
     * @param connected true if network is available, false otherwise
     */
    void onNetworkStatusChanged(boolean connected);
    
    /**
     * Called when the CRS availability status changes.
     * 
     * @param available true if CRS is available, false otherwise
     */
    void onCRSStatusChanged(boolean available);
    
    /**
     * Called when a retry attempt is made for a failed synchronization.
     * 
     * @param orderId the ID of the order being retried
     * @param attemptNumber the retry attempt number (1-based)
     * @param maxAttempts the maximum number of retry attempts
     * @param operation the type of synchronization operation
     */
    void onRetryAttempt(UUID orderId, int attemptNumber, int maxAttempts, SyncOperation operation);
}

