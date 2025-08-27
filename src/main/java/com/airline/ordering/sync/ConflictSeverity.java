package com.airline.ordering.sync;

/**
 * Enumeration of conflict severity levels.
 */
public enum ConflictSeverity {
    
    /**
     * Low severity conflicts that can typically be auto-resolved.
     */
    LOW("Low", 1),
    
    /**
     * Medium severity conflicts that may require user review.
     */
    MEDIUM("Medium", 2),
    
    /**
     * High severity conflicts that require immediate user intervention.
     */
    HIGH("High", 3),
    
    /**
     * Critical conflicts that may affect order fulfillment.
     */
    CRITICAL("Critical", 4);
    
    private final String displayName;
    private final int priority;
    
    ConflictSeverity(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getPriority() {
        return priority;
    }
    
    /**
     * Checks if this severity level allows automatic resolution.
     */
    public boolean allowsAutoResolution() {
        return this == LOW;
    }
    
    /**
     * Checks if this severity level requires user notification.
     */
    public boolean requiresUserNotification() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Checks if this severity level blocks synchronization.
     */
    public boolean blocksSync() {
        return this == CRITICAL;
    }
    
    /**
     * Gets the recommended timeout for manual resolution in minutes.
     */
    public int getResolutionTimeoutMinutes() {
        switch (this) {
            case LOW:
                return 5;
            case MEDIUM:
                return 15;
            case HIGH:
                return 60;
            case CRITICAL:
                return 0; // No timeout - requires immediate attention
            default:
                return 15;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

