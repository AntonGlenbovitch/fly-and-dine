package com.airline.ordering.security.audit;

/**
 * Types of audit events that can be logged in the system.
 */
public enum AuditEventType {
    AUTHENTICATION("Authentication", "User or device authentication events"),
    AUTHORIZATION("Authorization", "Permission and access control events"),
    DATA_ACCESS("Data Access", "Data read and query events"),
    DATA_MODIFICATION("Data Modification", "Data create, update, and delete events"),
    SECURITY("Security", "Security-related events and violations"),
    SYSTEM("System", "System and application events"),
    CONFIGURATION("Configuration", "Configuration and settings changes"),
    SYNC("Synchronization", "Data synchronization events"),
    ERROR("Error", "Error and exception events"),
    PERFORMANCE("Performance", "Performance and monitoring events"),
    COMPLIANCE("Compliance", "Compliance and regulatory events"),
    USER_ACTION("User Action", "General user actions and interactions");
    
    private final String displayName;
    private final String description;
    
    AuditEventType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

