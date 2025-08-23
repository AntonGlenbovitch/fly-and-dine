package com.airline.ordering.domain;

/**
 * Enumeration representing the status of an order throughout its lifecycle.
 */
public enum OrderStatus {
    DRAFT("Draft"),
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    READY("Ready"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    FAILED("Failed");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isModifiable() {
        return this == DRAFT || this == PENDING;
    }
    
    public boolean isCancellable() {
        return this == DRAFT || this == PENDING || this == CONFIRMED;
    }
    
    public boolean isActive() {
        return this != CANCELLED && this != DELIVERED && this != FAILED;
    }
    
    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED;
    }
    
    public boolean requiresSync() {
        return this == PENDING || this == CONFIRMED || this == CANCELLED;
    }
}

