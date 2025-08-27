package com.airline.ordering.sync;

/**
 * Enumeration of different types of synchronization operations.
 */
public enum SyncOperation {
    
    /**
     * Full bidirectional synchronization - both push and pull operations.
     */
    FULL_SYNC("Full Synchronization"),
    
    /**
     * Push local changes to the CRS.
     */
    PUSH("Push to CRS"),
    
    /**
     * Pull updates from the CRS.
     */
    PULL("Pull from CRS"),
    
    /**
     * Synchronize a single order.
     */
    SINGLE_ORDER("Single Order Sync"),
    
    /**
     * Synchronize a batch of orders.
     */
    BATCH_SYNC("Batch Synchronization"),
    
    /**
     * Conflict resolution operation.
     */
    CONFLICT_RESOLUTION("Conflict Resolution"),
    
    /**
     * Data validation operation.
     */
    VALIDATION("Data Validation"),
    
    /**
     * Retry a previously failed synchronization.
     */
    RETRY("Retry Synchronization"),
    
    /**
     * Emergency synchronization for critical orders.
     */
    EMERGENCY_SYNC("Emergency Synchronization"),
    
    /**
     * Scheduled background synchronization.
     */
    BACKGROUND_SYNC("Background Synchronization");
    
    private final String displayName;
    
    SyncOperation(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this operation involves pushing data to CRS.
     */
    public boolean isPushOperation() {
        return this == PUSH || this == FULL_SYNC || this == SINGLE_ORDER || 
               this == BATCH_SYNC || this == EMERGENCY_SYNC || this == BACKGROUND_SYNC;
    }
    
    /**
     * Checks if this operation involves pulling data from CRS.
     */
    public boolean isPullOperation() {
        return this == PULL || this == FULL_SYNC || this == BACKGROUND_SYNC;
    }
    
    /**
     * Checks if this operation can be retried automatically.
     */
    public boolean isRetryable() {
        return this != CONFLICT_RESOLUTION && this != VALIDATION;
    }
    
    /**
     * Checks if this operation requires immediate execution.
     */
    public boolean isHighPriority() {
        return this == EMERGENCY_SYNC || this == SINGLE_ORDER;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

