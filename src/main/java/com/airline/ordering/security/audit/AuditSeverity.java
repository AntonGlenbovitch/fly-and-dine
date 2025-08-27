package com.airline.ordering.security.audit;

/**
 * Severity levels for audit events.
 */
public enum AuditSeverity {
    CRITICAL("Critical", "Critical security or system events requiring immediate attention", 5),
    HIGH("High", "High priority events that should be reviewed promptly", 4),
    MEDIUM("Medium", "Medium priority events for regular monitoring", 3),
    LOW("Low", "Low priority informational events", 2),
    INFO("Info", "General informational events", 1);
    
    private final String displayName;
    private final String description;
    private final int level;
    
    AuditSeverity(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean isHigherThan(AuditSeverity other) {
        return this.level > other.level;
    }
    
    public boolean isLowerThan(AuditSeverity other) {
        return this.level < other.level;
    }
    
    public boolean isAtLeast(AuditSeverity threshold) {
        return this.level >= threshold.level;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

