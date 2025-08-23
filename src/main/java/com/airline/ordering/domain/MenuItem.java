package com.airline.ordering.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a menu item available for ordering during flight.
 */
public class MenuItem {
    
    @NotNull
    private UUID itemId;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    
    @NotNull
    private MenuCategory category;
    
    @NotNull
    private Set<PassengerType> availableForTypes;
    
    private Set<String> allergens;
    
    private Set<String> dietaryTags; // vegetarian, vegan, gluten-free, etc.
    
    @NotNull
    private ItemStatus status;
    
    @PositiveOrZero
    private int inventoryCount;
    
    private Set<UUID> substitutableItems; // Items that can be substituted for this one
    
    private Set<UUID> comboItems; // Items that can be ordered together as a combo
    
    @NotNull
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public MenuItem() {
        this.itemId = UUID.randomUUID();
        this.availableForTypes = new HashSet<>();
        this.allergens = new HashSet<>();
        this.dietaryTags = new HashSet<>();
        this.substitutableItems = new HashSet<>();
        this.comboItems = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ItemStatus.AVAILABLE;
    }
    
    public MenuItem(String name, String description, BigDecimal price, MenuCategory category) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    
    // Getters and Setters
    public UUID getItemId() {
        return itemId;
    }
    
    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }
    
    public MenuCategory getCategory() {
        return category;
    }
    
    public void setCategory(MenuCategory category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<PassengerType> getAvailableForTypes() {
        return new HashSet<>(availableForTypes);
    }
    
    public void setAvailableForTypes(Set<PassengerType> availableForTypes) {
        this.availableForTypes = new HashSet<>(availableForTypes);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addAvailableForType(PassengerType type) {
        this.availableForTypes.add(type);
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<String> getAllergens() {
        return new HashSet<>(allergens);
    }
    
    public void setAllergens(Set<String> allergens) {
        this.allergens = new HashSet<>(allergens);
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<String> getDietaryTags() {
        return new HashSet<>(dietaryTags);
    }
    
    public void setDietaryTags(Set<String> dietaryTags) {
        this.dietaryTags = new HashSet<>(dietaryTags);
        this.updatedAt = LocalDateTime.now();
    }
    
    public ItemStatus getStatus() {
        return status;
    }
    
    public void setStatus(ItemStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getInventoryCount() {
        return inventoryCount;
    }
    
    public void setInventoryCount(int inventoryCount) {
        this.inventoryCount = inventoryCount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<UUID> getSubstitutableItems() {
        return new HashSet<>(substitutableItems);
    }
    
    public void setSubstitutableItems(Set<UUID> substitutableItems) {
        this.substitutableItems = new HashSet<>(substitutableItems);
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<UUID> getComboItems() {
        return new HashSet<>(comboItems);
    }
    
    public void setComboItems(Set<UUID> comboItems) {
        this.comboItems = new HashSet<>(comboItems);
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public boolean isAvailableFor(PassengerType passengerType) {
        return availableForTypes.contains(passengerType) && status == ItemStatus.AVAILABLE;
    }
    
    public boolean isInStock() {
        return inventoryCount > 0 && status == ItemStatus.AVAILABLE;
    }
    
    public boolean canSubstituteWith(UUID itemId) {
        return substitutableItems.contains(itemId);
    }
    
    public boolean canComboWith(UUID itemId) {
        return comboItems.contains(itemId);
    }
    
    public boolean hasAllergen(String allergen) {
        return allergens.contains(allergen.toLowerCase());
    }
    
    public boolean hasDietaryTag(String tag) {
        return dietaryTags.contains(tag.toLowerCase());
    }
    
    public void decrementInventory() {
        if (inventoryCount > 0) {
            inventoryCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void incrementInventory() {
        inventoryCount++;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(itemId, menuItem.itemId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
    
    @Override
    public String toString() {
        return "MenuItem{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", status=" + status +
                ", inventoryCount=" + inventoryCount +
                '}';
    }
}

