package com.airline.ordering.repository.impl;

import com.airline.ordering.domain.ItemStatus;
import com.airline.ordering.domain.MenuCategory;
import com.airline.ordering.domain.MenuItem;
import com.airline.ordering.domain.PassengerType;
import com.airline.ordering.repository.MenuItemRepository;
import com.airline.ordering.repository.SQLiteConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MenuItemRepositoryImpl implements MenuItemRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuItemRepositoryImpl.class);
    private final ObjectMapper objectMapper;
    
    public MenuItemRepositoryImpl() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public MenuItem save(MenuItem menuItem) {
        String sql = "INSERT INTO menu_items(item_id, name, description, price, category, available_for_types, allergens, dietary_tags, status, inventory_count, substitutable_items, combo_items, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                     + " ON CONFLICT(item_id) DO UPDATE SET name=?, description=?, price=?, category=?, available_for_types=?, allergens=?, dietary_tags=?, status=?, inventory_count=?, substitutable_items=?, combo_items=?, updated_at=?";
        
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String availableForTypesJson = objectMapper.writeValueAsString(menuItem.getAvailableForTypes().stream().map(Enum::name).collect(Collectors.toSet()));
            String allergensJson = objectMapper.writeValueAsString(menuItem.getAllergens());
            String dietaryTagsJson = objectMapper.writeValueAsString(menuItem.getDietaryTags());
            String substitutableItemsJson = objectMapper.writeValueAsString(menuItem.getSubstitutableItems());
            String comboItemsJson = objectMapper.writeValueAsString(menuItem.getComboItems());
            
            pstmt.setString(1, menuItem.getItemId().toString());
            pstmt.setString(2, menuItem.getName());
            pstmt.setString(3, menuItem.getDescription());
            pstmt.setDouble(4, menuItem.getPrice().doubleValue());
            pstmt.setString(5, menuItem.getCategory().name());
            pstmt.setString(6, availableForTypesJson);
            pstmt.setString(7, allergensJson);
            pstmt.setString(8, dietaryTagsJson);
            pstmt.setString(9, menuItem.getStatus().name());
            pstmt.setInt(10, menuItem.getInventoryCount());
            pstmt.setString(11, substitutableItemsJson);
            pstmt.setString(12, comboItemsJson);
            pstmt.setString(13, menuItem.getCreatedAt().toString());
            pstmt.setString(14, LocalDateTime.now().toString());
            
            // For ON CONFLICT UPDATE part
            pstmt.setString(15, menuItem.getName());
            pstmt.setString(16, menuItem.getDescription());
            pstmt.setDouble(17, menuItem.getPrice().doubleValue());
            pstmt.setString(18, menuItem.getCategory().name());
            pstmt.setString(19, availableForTypesJson);
            pstmt.setString(20, allergensJson);
            pstmt.setString(21, dietaryTagsJson);
            pstmt.setString(22, menuItem.getStatus().name());
            pstmt.setInt(23, menuItem.getInventoryCount());
            pstmt.setString(24, substitutableItemsJson);
            pstmt.setString(25, comboItemsJson);
            pstmt.setString(26, LocalDateTime.now().toString());
            
            pstmt.executeUpdate();
            logger.info("MenuItem saved: {}", menuItem.getItemId());
            return menuItem;
        } catch (SQLException | JsonProcessingException e) {
            logger.error("Error saving MenuItem {}: {}", menuItem.getItemId(), e.getMessage());
            throw new RuntimeException("Error saving MenuItem", e);
        }
    }
    
    @Override
    public Optional<MenuItem> findById(UUID id) {
        String sql = "SELECT * FROM menu_items WHERE item_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding MenuItem by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error finding MenuItem by ID", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<MenuItem> findAll() {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                menuItems.add(mapRowToMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all MenuItems: {}", e.getMessage());
            throw new RuntimeException("Error finding all MenuItems", e);
        }
        return menuItems;
    }
    
    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
            logger.info("MenuItem deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting MenuItem by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting MenuItem by ID", e);
        }
    }
    
    @Override
    public void delete(MenuItem entity) {
        deleteById(entity.getItemId());
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM menu_items";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting MenuItems: {}", e.getMessage());
            throw new RuntimeException("Error counting MenuItems", e);
        }
        return 0;
    }
    
    @Override
    public List<MenuItem> findByCategory(MenuCategory category) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE category = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItems.add(mapRowToMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding MenuItems by category {}: {}", category, e.getMessage());
            throw new RuntimeException("Error finding MenuItems by category", e);
        }
        return menuItems;
    }
    
    @Override
    public List<MenuItem> findByStatus(ItemStatus status) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE status = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItems.add(mapRowToMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding MenuItems by status {}: {}", status, e.getMessage());
            throw new RuntimeException("Error finding MenuItems by status", e);
        }
        return menuItems;
    }
    
    @Override
    public List<MenuItem> searchByNameOrDescription(String searchTerm) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItems.add(mapRowToMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching MenuItems by name or description {}: {}", searchTerm, e.getMessage());
            throw new RuntimeException("Error searching MenuItems", e);
        }
        return menuItems;
    }
    
    private MenuItem mapRowToMenuItem(ResultSet rs) throws SQLException {
        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(UUID.fromString(rs.getString("item_id")));
        menuItem.setName(rs.getString("name"));
        menuItem.setDescription(rs.getString("description"));
        menuItem.setPrice(rs.getBigDecimal("price"));
        menuItem.setCategory(MenuCategory.valueOf(rs.getString("category")));
        menuItem.setStatus(ItemStatus.valueOf(rs.getString("status")));
        menuItem.setInventoryCount(rs.getInt("inventory_count"));
        menuItem.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
        menuItem.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at")));
        
        try {
            Set<String> availableForTypesStrings = objectMapper.readValue(rs.getString("available_for_types"),
                                                                         objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
            menuItem.setAvailableForTypes(availableForTypesStrings.stream().map(PassengerType::valueOf).collect(Collectors.toSet()));
            
            Set<String> allergens = objectMapper.readValue(rs.getString("allergens"),
                                                           objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
            menuItem.setAllergens(allergens);
            
            Set<String> dietaryTags = objectMapper.readValue(rs.getString("dietary_tags"),
                                                             objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
            menuItem.setDietaryTags(dietaryTags);
            
            Set<UUID> substitutableItems = objectMapper.readValue(rs.getString("substitutable_items"),
                                                                  objectMapper.getTypeFactory().constructCollectionType(Set.class, UUID.class));
            menuItem.setSubstitutableItems(substitutableItems);
            
            Set<UUID> comboItems = objectMapper.readValue(rs.getString("combo_items"),
                                                          objectMapper.getTypeFactory().constructCollectionType(Set.class, UUID.class));
            menuItem.setComboItems(comboItems);
            
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON from MenuItem ResultSet: {}", e.getMessage());
            // Handle error, perhaps return an empty set or re-throw as a runtime exception
        }
        
        return menuItem;
    }
}

