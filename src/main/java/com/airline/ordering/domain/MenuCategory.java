package com.airline.ordering.domain;

/**
 * Enumeration representing different categories of menu items.
 */
public enum MenuCategory {
    APPETIZER("Appetizer", 1),
    MAIN_COURSE("Main Course", 2),
    DESSERT("Dessert", 3),
    BEVERAGE("Beverage", 4),
    SNACK("Snack", 5),
    ALCOHOL("Alcohol", 6),
    SPECIAL_MEAL("Special Meal", 7);
    
    private final String displayName;
    private final int sortOrder;
    
    MenuCategory(String displayName, int sortOrder) {
        this.displayName = displayName;
        this.sortOrder = sortOrder;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public boolean isAlcoholic() {
        return this == ALCOHOL;
    }
    
    public boolean isFood() {
        return this == APPETIZER || this == MAIN_COURSE || this == DESSERT || 
               this == SNACK || this == SPECIAL_MEAL;
    }
    
    public boolean isBeverage() {
        return this == BEVERAGE || this == ALCOHOL;
    }
}

