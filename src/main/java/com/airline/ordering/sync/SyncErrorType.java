package com.airline.ordering.sync;

/**
 * Enumeration of different types of synchronization errors.
 */
public enum SyncErrorType {
    
    /**
     * Network connectivity issues.
     */
    NETWORK_ERROR("Network Error", true),
    
    /**
     * Data validation failures.
     */
    VALIDATION_ERROR("Validation Error", false),
    
    /**
     * Data conflicts between local and remote versions.
     */
    CONFLICT_ERROR("Conflict Error", false),
    
    /**
     * Authentication or authorization failures.
     */
    AUTHENTICATION_ERROR("Authentication Error", false),
    
    /**
     * Server-side errors from the CRS.
     */
    SERVER_ERROR("Server Error", true),
    
    /**
     * Data corruption or integrity issues.
     */
    DATA_ERROR("Data Error", false),
    
    /**
     * Operation timeout errors.
     */
    TIMEOUT_ERROR("Timeout Error", true),
    
    /**
     * Rate limiting or quota exceeded errors.
     */
    RATE_LIMIT_ERROR("Rate Limit Error", true),
    
    /**
     * Business rule violations.
     */
    BUSINESS_RULE_ERROR("Business Rule Error", false),
    
    /**
     * Unknown or unexpected errors.
     */
    UNKNOWN_ERROR("Unknown Error", true);
    
    private final String displayName;
    private final boolean defaultRetryable;
    
    SyncErrorType(String displayName, boolean defaultRetryable) {
        this.displayName = displayName;
        this.defaultRetryable = defaultRetryable;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isDefaultRetryable() {
        return defaultRetryable;
    }
    
    /**
     * Checks if this error type typically requires user intervention.
     */
    public boolean requiresUserIntervention() {
        return this == CONFLICT_ERROR || this == AUTHENTICATION_ERROR || 
               this == BUSINESS_RULE_ERROR || this == DATA_ERROR;
    }
    
    /**
     * Checks if this error type is typically temporary.
     */
    public boolean isTemporary() {
        return this == NETWORK_ERROR || this == SERVER_ERROR || 
               this == TIMEOUT_ERROR || this == RATE_LIMIT_ERROR;
    }
    
    /**
     * Gets the recommended retry delay in seconds for this error type.
     */
    public int getRecommendedRetryDelaySeconds() {
        switch (this) {
            case NETWORK_ERROR:
            case TIMEOUT_ERROR:
                return 30;
            case SERVER_ERROR:
                return 60;
            case RATE_LIMIT_ERROR:
                return 300; // 5 minutes
            default:
                return 0; // No retry
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

