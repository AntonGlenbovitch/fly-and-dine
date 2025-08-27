package com.airline.ordering.repository;

import com.airline.ordering.domain.MenuItem;
import com.airline.ordering.domain.MenuCategory;
import com.airline.ordering.domain.ItemStatus;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for MenuItem entities.
 */
public interface MenuItemRepository extends Repository<MenuItem, UUID> {
    
    /**
     * Finds all menu items by category.
     * @param category The category of the menu item.
     * @return A list of menu items in the specified category.
     */
    List<MenuItem> findByCategory(MenuCategory category);
    
    /**
     * Finds all menu items by status.
     * @param status The status of the menu item.
     * @return A list of menu items with the specified status.
     */
    List<MenuItem> findByStatus(ItemStatus status);
    
    /**
     * Searches for menu items by name or description.
     * @param searchTerm The term to search for.
     * @return A list of matching menu items.
     */
    List<MenuItem> searchByNameOrDescription(String searchTerm);
}

