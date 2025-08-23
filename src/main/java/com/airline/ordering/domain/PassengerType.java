package com.airline.ordering.domain;

/**
 * Enumeration representing different types of passengers.
 * Used for determining service levels and available menu items.
 */
public enum PassengerType {
    ECONOMY("Economy", 1),
    PREMIUM_ECONOMY("Premium Economy", 2),
    BUSINESS("Business", 3),
    FIRST_CLASS("First Class", 4),
    CREW("Crew", 5);
    
    private final String displayName;
    private final int serviceLevel;
    
    PassengerType(String displayName, int serviceLevel) {
        this.displayName = displayName;
        this.serviceLevel = serviceLevel;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getServiceLevel() {
        return serviceLevel;
    }
    
    public boolean isVip() {
        return this == BUSINESS || this == FIRST_CLASS;
    }
    
    public boolean isCrew() {
        return this == CREW;
    }
    
    public boolean canAccessPremiumMenu() {
        return serviceLevel >= PREMIUM_ECONOMY.serviceLevel;
    }
}

