package com.airline.ordering.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MenuItem domain entity.
 */
class MenuItemTest {
    
    private MenuItem menuItem;
    
    @BeforeEach
    void setUp() {
        menuItem = new MenuItem("Chicken Sandwich", "Grilled chicken with lettuce and tomato", 
                               new BigDecimal("15.99"), MenuCategory.MAIN_COURSE);
    }
    
    @Test
    void testMenuItemCreation() {
        assertNotNull(menuItem.getItemId());
        assertEquals("Chicken Sandwich", menuItem.getName());
        assertEquals("Grilled chicken with lettuce and tomato", menuItem.getDescription());
        assertEquals(new BigDecimal("15.99"), menuItem.getPrice());
        assertEquals(MenuCategory.MAIN_COURSE, menuItem.getCategory());
        assertEquals(ItemStatus.AVAILABLE, menuItem.getStatus());
        assertEquals(0, menuItem.getInventoryCount());
        assertTrue(menuItem.getAvailableForTypes().isEmpty());
        assertTrue(menuItem.getAllergens().isEmpty());
        assertTrue(menuItem.getDietaryTags().isEmpty());
    }
    
    @Test
    void testAvailabilityForPassengerTypes() {
        assertFalse(menuItem.isAvailableFor(PassengerType.ECONOMY));
        
        menuItem.addAvailableForType(PassengerType.ECONOMY);
        menuItem.addAvailableForType(PassengerType.BUSINESS);
        
        assertTrue(menuItem.isAvailableFor(PassengerType.ECONOMY));
        assertTrue(menuItem.isAvailableFor(PassengerType.BUSINESS));
        assertFalse(menuItem.isAvailableFor(PassengerType.FIRST_CLASS));
    }
    
    @Test
    void testInventoryManagement() {
        assertFalse(menuItem.isInStock());
        
        menuItem.setInventoryCount(10);
        assertTrue(menuItem.isInStock());
        
        menuItem.decrementInventory();
        assertEquals(9, menuItem.getInventoryCount());
        
        menuItem.incrementInventory();
        assertEquals(10, menuItem.getInventoryCount());
        
        // Test decrement when count is 0
        menuItem.setInventoryCount(0);
        menuItem.decrementInventory();
        assertEquals(0, menuItem.getInventoryCount());
    }
    
    @Test
    void testStatusAffectsAvailability() {
        menuItem.addAvailableForType(PassengerType.ECONOMY);
        menuItem.setInventoryCount(5);
        
        assertTrue(menuItem.isAvailableFor(PassengerType.ECONOMY));
        assertTrue(menuItem.isInStock());
        
        menuItem.setStatus(ItemStatus.OUT_OF_STOCK);
        
        assertFalse(menuItem.isAvailableFor(PassengerType.ECONOMY));
        assertFalse(menuItem.isInStock());
    }
    
    @Test
    void testAllergenManagement() {
        assertFalse(menuItem.hasAllergen("nuts"));
        
        menuItem.setAllergens(Set.of("nuts", "dairy"));
        
        assertTrue(menuItem.hasAllergen("nuts"));
        assertTrue(menuItem.hasAllergen("dairy"));
        assertTrue(menuItem.hasAllergen("NUTS")); // Case insensitive
        assertFalse(menuItem.hasAllergen("gluten"));
    }
    
    @Test
    void testDietaryTags() {
        assertFalse(menuItem.hasDietaryTag("vegetarian"));
        
        menuItem.setDietaryTags(Set.of("vegetarian", "low-carb"));
        
        assertTrue(menuItem.hasDietaryTag("vegetarian"));
        assertTrue(menuItem.hasDietaryTag("low-carb"));
        assertTrue(menuItem.hasDietaryTag("VEGETARIAN")); // Case insensitive
        assertFalse(menuItem.hasDietaryTag("vegan"));
    }
    
    @Test
    void testSubstitutionManagement() {
        UUID substitutionItemId = UUID.randomUUID();
        
        assertFalse(menuItem.canSubstituteWith(substitutionItemId));
        
        menuItem.setSubstitutableItems(Set.of(substitutionItemId));
        
        assertTrue(menuItem.canSubstituteWith(substitutionItemId));
    }
    
    @Test
    void testComboManagement() {
        UUID comboItemId = UUID.randomUUID();
        
        assertFalse(menuItem.canComboWith(comboItemId));
        
        menuItem.setComboItems(Set.of(comboItemId));
        
        assertTrue(menuItem.canComboWith(comboItemId));
    }
    
    @Test
    void testMenuItemEquality() {
        MenuItem menuItem2 = new MenuItem("Different Item", "Different description", 
                                         new BigDecimal("10.00"), MenuCategory.APPETIZER);
        
        assertNotEquals(menuItem, menuItem2);
        assertEquals(menuItem, menuItem);
        assertEquals(menuItem.hashCode(), menuItem.hashCode());
    }
    
    @Test
    void testUpdatedAtChanges() {
        var originalUpdatedAt = menuItem.getUpdatedAt();
        
        // Small delay to ensure timestamp difference
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        menuItem.setName("Updated Name");
        
        assertTrue(menuItem.getUpdatedAt().isAfter(originalUpdatedAt));
    }
    
    @Test
    void testImmutableCollections() {
        UUID itemId = UUID.randomUUID();
        menuItem.setSubstitutableItems(Set.of(itemId));
        
        Set<UUID> substitutableItems = menuItem.getSubstitutableItems();
        
        // The returned collection should be a defensive copy, not immutable
        // So we can modify it, but it won't affect the original
        substitutableItems.add(UUID.randomUUID());
        
        // Original should still only have one item
        assertEquals(1, menuItem.getSubstitutableItems().size());
        assertTrue(menuItem.getSubstitutableItems().contains(itemId));
    }
}

