package com.airline.ordering.security.audit;

/**
 * Possible outcomes of audited events.
 */
public enum AuditOutcome {
    SUCCESS("Success", "Operation completed successfully"),
    FAILURE("Failure", "Operation failed due to business logic or validation"),
    ERROR("Error", "Operation failed due to system error or exception"),
    PARTIAL("Partial", "Operation completed partially with some issues"),
    DENIED("Denied", "Operation was denied due to insufficient permissions"),
    TIMEOUT("Timeout", "Operation timed out"),
    CANCELLED("Cancelled", "Operation was cancelled by user or system"),
    UNKNOWN("Unknown", "Outcome is unknown or undetermined");
    
    private final String displayName;
    private final String description;
    
    AuditOutcome(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isSuccessful() {
        return this == SUCCESS || this == PARTIAL;
    }
    
    public boolean isFailure() {
        return this == FAILURE || this == ERROR || this == DENIED || this == TIMEOUT;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

