package com.airline.ordering.domain;

/**
 * Enumeration representing the status of individual order items.
 */
public enum OrderItemStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    READY("Ready"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    SUBSTITUTED("Substituted");
    
    private final String displayName;
    
    OrderItemStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isModifiable() {
        return this == PENDING;
    }
    
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }
    
    public boolean isActive() {
        return this != CANCELLED && this != DELIVERED && this != SUBSTITUTED;
    }
}

