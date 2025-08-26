package com.airline.ordering.repository.impl;

import com.airline.ordering.domain.ItemStatus;
import com.airline.ordering.domain.MenuCategory;
import com.airline.ordering.domain.MenuItem;
import com.airline.ordering.domain.PassengerType;
import com.airline.ordering.repository.SQLiteConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemRepositoryImplTest {
    
    private MenuItemRepositoryImpl menuItemRepository;
    private static final String DB_FILE = "inflight_ordering.db";
    
    @BeforeEach
    void setUp() {
        // Ensure a clean database for each test
        File dbFile = new File(DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        SQLiteConnection.initializeDatabase();
        menuItemRepository = new MenuItemRepositoryImpl();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up database file after each test
        File dbFile = new File(DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }
    
    @Test
    void testSaveAndFindById() {
        MenuItem item = new MenuItem("Coffee", "Hot coffee", new BigDecimal("3.50"), MenuCategory.BEVERAGE);
        item.setInventoryCount(100);
        item.addAvailableForType(PassengerType.ECONOMY);
        item.setAllergens(Set.of("caffeine"));
        item.setDietaryTags(Set.of("vegan"));
        
        MenuItem savedItem = menuItemRepository.save(item);
        
        assertNotNull(savedItem);
        assertEquals(item.getItemId(), savedItem.getItemId());
        
        Optional<MenuItem> foundItem = menuItemRepository.findById(item.getItemId());
        assertTrue(foundItem.isPresent());
        assertEquals(item.getName(), foundItem.get().getName());
        assertEquals(item.getInventoryCount(), foundItem.get().getInventoryCount());
        assertTrue(foundItem.get().getAvailableForTypes().contains(PassengerType.ECONOMY));
        assertTrue(foundItem.get().getAllergens().contains("caffeine"));
        assertTrue(foundItem.get().getDietaryTags().contains("vegan"));
    }
    
    @Test
    void testUpdateMenuItem() {
        MenuItem item = new MenuItem("Tea", "Hot tea", new BigDecimal("3.00"), MenuCategory.BEVERAGE);
        menuItemRepository.save(item);
        
        item.setPrice(new BigDecimal("4.00"));
        item.setInventoryCount(50);
        item.setStatus(ItemStatus.OUT_OF_STOCK);
        menuItemRepository.save(item);
        
        Optional<MenuItem> updatedItem = menuItemRepository.findById(item.getItemId());
        assertTrue(updatedItem.isPresent());
        assertEquals(new BigDecimal("4.00").setScale(2, BigDecimal.ROUND_HALF_UP), updatedItem.get().getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
        assertEquals(50, updatedItem.get().getInventoryCount());
        assertEquals(ItemStatus.OUT_OF_STOCK, updatedItem.get().getStatus());
    }
    
    @Test
    void testFindAll() {
        MenuItem item1 = new MenuItem("Water", "Bottled water", new BigDecimal("2.00"), MenuCategory.BEVERAGE);
        MenuItem item2 = new MenuItem("Sandwich", "Club sandwich", new BigDecimal("10.00"), MenuCategory.MAIN_COURSE);
        menuItemRepository.save(item1);
        menuItemRepository.save(item2);
        
        List<MenuItem> items = menuItemRepository.findAll();
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Water")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Sandwich")));
    }
    
    @Test
    void testDeleteById() {
        MenuItem item = new MenuItem("Juice", "Orange juice", new BigDecimal("3.00"), MenuCategory.BEVERAGE);
        menuItemRepository.save(item);
        
        menuItemRepository.deleteById(item.getItemId());
        
        Optional<MenuItem> foundItem = menuItemRepository.findById(item.getItemId());
        assertFalse(foundItem.isPresent());
    }
    
    @Test
    void testCount() {
        assertEquals(0, menuItemRepository.count());
        
        menuItemRepository.save(new MenuItem("Soda", "Cola", new BigDecimal("2.50"), MenuCategory.BEVERAGE));
        assertEquals(1, menuItemRepository.count());
        
        menuItemRepository.save(new MenuItem("Chips", "Potato chips", new BigDecimal("1.50"), MenuCategory.SNACK));
        assertEquals(2, menuItemRepository.count());
    }
    
    @Test
    void testFindByCategory() {
        MenuItem item1 = new MenuItem("Burger", "Beef burger", new BigDecimal("12.00"), MenuCategory.MAIN_COURSE);
        MenuItem item2 = new MenuItem("Fries", "French fries", new BigDecimal("5.00"), MenuCategory.SNACK);
        MenuItem item3 = new MenuItem("Pizza", "Pepperoni pizza", new BigDecimal("15.00"), MenuCategory.MAIN_COURSE);
        menuItemRepository.save(item1);
        menuItemRepository.save(item2);
        menuItemRepository.save(item3);
        
        List<MenuItem> mainCourses = menuItemRepository.findByCategory(MenuCategory.MAIN_COURSE);
        assertEquals(2, mainCourses.size());
        assertTrue(mainCourses.stream().anyMatch(i -> i.getName().equals("Burger")));
        assertTrue(mainCourses.stream().anyMatch(i -> i.getName().equals("Pizza")));
    }
    
    @Test
    void testFindByStatus() {
        MenuItem item1 = new MenuItem("Salad", "Garden salad", new BigDecimal("8.00"), MenuCategory.APPETIZER);
        item1.setStatus(ItemStatus.AVAILABLE);
        MenuItem item2 = new MenuItem("Soup", "Tomato soup", new BigDecimal("6.00"), MenuCategory.APPETIZER);
        item2.setStatus(ItemStatus.OUT_OF_STOCK);
        menuItemRepository.save(item1);
        menuItemRepository.save(item2);
        
        List<MenuItem> availableItems = menuItemRepository.findByStatus(ItemStatus.AVAILABLE);
        assertEquals(1, availableItems.size());
        assertEquals("Salad", availableItems.get(0).getName());
    }
    
    @Test
    void testSearchByNameOrDescription() {
        MenuItem item1 = new MenuItem("Chicken Wrap", "Grilled chicken wrap", new BigDecimal("9.00"), MenuCategory.MAIN_COURSE);
        MenuItem item2 = new MenuItem("Veggie Burger", "Plant-based burger", new BigDecimal("11.00"), MenuCategory.MAIN_COURSE);
        MenuItem item3 = new MenuItem("Chocolate Cake", "Rich chocolate dessert", new BigDecimal("7.00"), MenuCategory.DESSERT);
        menuItemRepository.save(item1);
        menuItemRepository.save(item2);
        menuItemRepository.save(item3);
        
        List<MenuItem> results1 = menuItemRepository.searchByNameOrDescription("chicken");
        assertEquals(1, results1.size());
        assertEquals("Chicken Wrap", results1.get(0).getName());
        
        List<MenuItem> results2 = menuItemRepository.searchByNameOrDescription("burger");
        assertEquals(1, results2.size());
        assertTrue(results2.stream().anyMatch(i -> i.getName().equals("Veggie Burger")));
        
        List<MenuItem> results3 = menuItemRepository.searchByNameOrDescription("dessert");
        assertEquals(1, results3.size());
        assertEquals("Chocolate Cake", results3.get(0).getName());
    }
}

