package com.airline.ordering.sync;

/**
 * Enumeration of different types of data conflicts that can occur during synchronization.
 */
public enum ConflictType {
    
    /**
     * Order status has been modified both locally and remotely.
     */
    STATUS_CONFLICT("Status Conflict", false),
    
    /**
     * Order content (items, quantities, etc.) has been modified both locally and remotely.
     */
    CONTENT_CONFLICT("Content Conflict", false),
    
    /**
     * Order timestamps indicate conflicting modification times.
     */
    TIMESTAMP_CONFLICT("Timestamp Conflict", true),
    
    /**
     * Order has been deleted remotely but modified locally, or vice versa.
     */
    DELETION_CONFLICT("Deletion Conflict", false),
    
    /**
     * Order version numbers are out of sync.
     */
    VERSION_CONFLICT("Version Conflict", true),
    
    /**
     * Order has been confirmed remotely but is still being modified locally.
     */
    CONFIRMATION_CONFLICT("Confirmation Conflict", false),
    
    /**
     * Order payment status conflicts between local and remote.
     */
    PAYMENT_CONFLICT("Payment Conflict", false),
    
    /**
     * Order delivery information conflicts.
     */
    DELIVERY_CONFLICT("Delivery Conflict", true),
    
    /**
     * Order passenger information conflicts.
     */
    PASSENGER_CONFLICT("Passenger Conflict", false),
    
    /**
     * Order seat assignment conflicts.
     */
    SEAT_CONFLICT("Seat Conflict", false);
    
    private final String displayName;
    private final boolean autoResolvable;
    
    ConflictType(String displayName, boolean autoResolvable) {
        this.displayName = displayName;
        this.autoResolvable = autoResolvable;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isAutoResolvable() {
        return autoResolvable;
    }
    
    /**
     * Checks if this conflict type affects order processing.
     */
    public boolean affectsOrderProcessing() {
        return this == STATUS_CONFLICT || this == CONTENT_CONFLICT || 
               this == CONFIRMATION_CONFLICT || this == PAYMENT_CONFLICT;
    }
    
    /**
     * Checks if this conflict type requires immediate attention.
     */
    public boolean requiresImmediateAttention() {
        return this == DELETION_CONFLICT || this == CONFIRMATION_CONFLICT || 
               this == PAYMENT_CONFLICT;
    }
    
    /**
     * Gets the recommended resolution strategy for this conflict type.
     */
    public ConflictResolutionStrategy getRecommendedStrategy() {
        switch (this) {
            case TIMESTAMP_CONFLICT:
            case VERSION_CONFLICT:
                return ConflictResolutionStrategy.LAST_WRITE_WINS;
                
            case DELIVERY_CONFLICT:
                return ConflictResolutionStrategy.MERGE;
                
            case STATUS_CONFLICT:
            case CONTENT_CONFLICT:
            case DELETION_CONFLICT:
            case CONFIRMATION_CONFLICT:
            case PAYMENT_CONFLICT:
            case PASSENGER_CONFLICT:
            case SEAT_CONFLICT:
                return ConflictResolutionStrategy.MANUAL_RESOLUTION;
                
            default:
                return ConflictResolutionStrategy.MANUAL_RESOLUTION;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

