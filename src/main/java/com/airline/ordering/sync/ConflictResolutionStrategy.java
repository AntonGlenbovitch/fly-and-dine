package com.airline.ordering.sync;

/**
 * Enumeration of different conflict resolution strategies.
 */
public enum ConflictResolutionStrategy {
    
    /**
     * Use the local version of the data.
     */
    USE_LOCAL("Use Local Version", true),
    
    /**
     * Use the remote version of the data.
     */
    USE_REMOTE("Use Remote Version", true),
    
    /**
     * Use the version with the most recent timestamp.
     */
    LAST_WRITE_WINS("Last Write Wins", true),
    
    /**
     * Merge both versions intelligently.
     */
    MERGE("Merge Versions", true),
    
    /**
     * Require manual resolution by a user.
     */
    MANUAL_RESOLUTION("Manual Resolution", false),
    
    /**
     * Use the version with higher priority (e.g., confirmed orders take precedence).
     */
    PRIORITY_BASED("Priority Based", true),
    
    /**
     * Create a new version that combines both changes.
     */
    CREATE_NEW("Create New Version", false),
    
    /**
     * Reject the conflicting change and keep the existing version.
     */
    REJECT_CHANGE("Reject Change", true),
    
    /**
     * Escalate to a supervisor or manager for resolution.
     */
    ESCALATE("Escalate for Resolution", false);
    
    private final String displayName;
    private final boolean automatic;
    
    ConflictResolutionStrategy(String displayName, boolean automatic) {
        this.displayName = displayName;
        this.automatic = automatic;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isAutomatic() {
        return automatic;
    }
    
    /**
     * Checks if this strategy preserves local changes.
     */
    public boolean preservesLocalChanges() {
        return this == USE_LOCAL || this == MERGE || this == CREATE_NEW;
    }
    
    /**
     * Checks if this strategy preserves remote changes.
     */
    public boolean preservesRemoteChanges() {
        return this == USE_REMOTE || this == MERGE || this == CREATE_NEW;
    }
    
    /**
     * Checks if this strategy requires user input.
     */
    public boolean requiresUserInput() {
        return this == MANUAL_RESOLUTION || this == CREATE_NEW || this == ESCALATE;
    }
    
    /**
     * Gets the recommended strategy based on conflict type and severity.
     */
    public static ConflictResolutionStrategy getRecommended(ConflictType type, ConflictSeverity severity) {
        // High severity conflicts typically require manual resolution
        if (severity == ConflictSeverity.HIGH || severity == ConflictSeverity.CRITICAL) {
            return MANUAL_RESOLUTION;
        }
        
        // Use the conflict type's recommended strategy
        return type.getRecommendedStrategy();
    }
    
    /**
     * Gets the fallback strategy if the primary strategy fails.
     */
    public ConflictResolutionStrategy getFallbackStrategy() {
        switch (this) {
            case MERGE:
                return LAST_WRITE_WINS;
            case PRIORITY_BASED:
                return USE_REMOTE;
            case CREATE_NEW:
                return MANUAL_RESOLUTION;
            case ESCALATE:
                return MANUAL_RESOLUTION;
            default:
                return MANUAL_RESOLUTION;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

