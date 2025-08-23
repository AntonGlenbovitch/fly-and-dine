package com.airline.ordering.domain;

/**
 * Enumeration representing the status of menu items.
 */
public enum ItemStatus {
    AVAILABLE("Available"),
    OUT_OF_STOCK("Out of Stock"),
    DISCONTINUED("Discontinued"),
    TEMPORARILY_UNAVAILABLE("Temporarily Unavailable");
    
    private final String displayName;
    
    ItemStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isOrderable() {
        return this == AVAILABLE;
    }
}

