package com.airline.ordering.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an individual item within an order.
 */
public class OrderItem {
    
    @NotNull
    private UUID orderItemId;
    
    @NotNull
    private UUID menuItemId;
    
    @NotNull
    private String menuItemName; // Denormalized for performance and offline capability
    
    @NotNull
    @Positive
    private int quantity;
    
    @NotNull
    private BigDecimal unitPrice;
    
    @NotNull
    private BigDecimal totalPrice;
    
    private String specialInstructions;
    
    private UUID substitutedFromItemId; // If this item is a substitution
    
    @NotNull
    private OrderItemStatus status;
    
    @NotNull
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public OrderItem() {
        this.orderItemId = UUID.randomUUID();
        this.quantity = 1;
        this.status = OrderItemStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public OrderItem(UUID menuItemId, String menuItemName, int quantity, BigDecimal unitPrice) {
        this();
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public UUID getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public UUID getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getMenuItemName() {
        return menuItemName;
    }
    
    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
        this.updatedAt = LocalDateTime.now();
    }
    
    public UUID getSubstitutedFromItemId() {
        return substitutedFromItemId;
    }
    
    public void setSubstitutedFromItemId(UUID substitutedFromItemId) {
        this.substitutedFromItemId = substitutedFromItemId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public OrderItemStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderItemStatus status) {
        this.status = status;
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
    public boolean isSubstitution() {
        return substitutedFromItemId != null;
    }
    
    public boolean hasSpecialInstructions() {
        return specialInstructions != null && !specialInstructions.trim().isEmpty();
    }
    
    public void incrementQuantity() {
        setQuantity(quantity + 1);
    }
    
    public void decrementQuantity() {
        if (quantity > 1) {
            setQuantity(quantity - 1);
        }
    }
    
    public void recalculateTotalPrice() {
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.updatedAt = LocalDateTime.now();
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(orderItemId, orderItem.orderItemId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderItemId);
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", menuItemName='" + menuItemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }
}

