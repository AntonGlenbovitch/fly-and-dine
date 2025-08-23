package com.airline.ordering.service;

import com.airline.ordering.domain.MenuItem;
import com.airline.ordering.domain.MenuCategory;
import com.airline.ordering.domain.PassengerType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing menu items and inventory.
 * Provides business logic for menu operations, validation, and inventory management.
 */
public interface MenuService {
    
    /**
     * Retrieves a menu item by its ID.
     * 
     * @param itemId The unique identifier of the menu item
     * @return Optional containing the menu item if found, empty otherwise
     */
    Optional<MenuItem> getMenuItem(UUID itemId);
    
    /**
     * Retrieves all menu items available for a specific passenger type.
     * 
     * @param passengerType The passenger type
     * @return List of available menu items
     */
    List<MenuItem> getAvailableMenuItems(PassengerType passengerType);
    
    /**
     * Retrieves menu items by category for a specific passenger type.
     * 
     * @param category The menu category
     * @param passengerType The passenger type
     * @return List of menu items in the category
     */
    List<MenuItem> getMenuItemsByCategory(MenuCategory category, PassengerType passengerType);
    
    /**
     * Searches menu items by name or description.
     * 
     * @param searchTerm The search term
     * @param passengerType The passenger type
     * @return List of matching menu items
     */
    List<MenuItem> searchMenuItems(String searchTerm, PassengerType passengerType);
    
    /**
     * Retrieves menu items that match specific dietary requirements.
     * 
     * @param dietaryTags Set of dietary tags (e.g., "vegetarian", "gluten-free")
     * @param passengerType The passenger type
     * @return List of matching menu items
     */
    List<MenuItem> getMenuItemsByDietaryRequirements(Set<String> dietaryTags, PassengerType passengerType);
    
    /**
     * Checks if a menu item is available for ordering.
     * 
     * @param itemId The menu item ID
     * @param passengerType The passenger type
     * @param quantity The requested quantity
     * @return true if available, false otherwise
     */
    boolean isItemAvailable(UUID itemId, PassengerType passengerType, int quantity);
    
    /**
     * Validates if two items can be ordered together as a combo.
     * 
     * @param itemId1 First item ID
     * @param itemId2 Second item ID
     * @return true if combo is valid, false otherwise
     */
    boolean isValidCombo(UUID itemId1, UUID itemId2);
    
    /**
     * Validates if multiple items can be ordered together as a combo.
     * 
     * @param itemIds List of item IDs
     * @return true if combo is valid, false otherwise
     */
    boolean isValidCombo(List<UUID> itemIds);
    
    /**
     * Checks if one item can be substituted for another.
     * 
     * @param originalItemId The original item ID
     * @param substitutionItemId The substitution item ID
     * @param passengerType The passenger type
     * @return true if substitution is allowed, false otherwise
     */
    boolean canSubstitute(UUID originalItemId, UUID substitutionItemId, PassengerType passengerType);
    
    /**
     * Gets all possible substitutions for a menu item.
     * 
     * @param itemId The menu item ID
     * @param passengerType The passenger type
     * @return List of possible substitution items
     */
    List<MenuItem> getPossibleSubstitutions(UUID itemId, PassengerType passengerType);
    
    /**
     * Reserves inventory for a menu item.
     * 
     * @param itemId The menu item ID
     * @param quantity The quantity to reserve
     * @return true if reservation successful, false if insufficient inventory
     */
    boolean reserveInventory(UUID itemId, int quantity);
    
    /**
     * Releases reserved inventory for a menu item.
     * 
     * @param itemId The menu item ID
     * @param quantity The quantity to release
     */
    void releaseInventory(UUID itemId, int quantity);
    
    /**
     * Updates inventory count for a menu item.
     * 
     * @param itemId The menu item ID
     * @param newCount The new inventory count
     */
    void updateInventory(UUID itemId, int newCount);
    
    /**
     * Gets current inventory count for a menu item.
     * 
     * @param itemId The menu item ID
     * @return Current inventory count
     */
    int getInventoryCount(UUID itemId);
    
    /**
     * Validates a list of order items for business rules compliance.
     * 
     * @param orderItems List of items being ordered
     * @param passengerType The passenger type
     * @return List of validation errors, empty if valid
     */
    List<String> validateOrderItems(List<UUID> orderItems, PassengerType passengerType);
    
    /**
     * Gets all menu categories available for a passenger type.
     * 
     * @param passengerType The passenger type
     * @return List of available categories
     */
    List<MenuCategory> getAvailableCategories(PassengerType passengerType);
    
    /**
     * Saves a menu item to the repository.
     * 
     * @param menuItem The menu item to save
     * @return The saved menu item
     */
    MenuItem saveMenuItem(MenuItem menuItem);
    
    /**
     * Deletes a menu item.
     * 
     * @param itemId The menu item ID to delete
     */
    void deleteMenuItem(UUID itemId);
}

