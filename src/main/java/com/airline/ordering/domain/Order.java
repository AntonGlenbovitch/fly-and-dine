package com.airline.ordering.domain;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a complete order placed by a passenger.
 * Aggregates multiple order items and manages order lifecycle.
 */
public class Order {
    
    @NotNull
    private UUID orderId;
    
    @NotNull
    private UUID passengerId;
    
    @NotNull
    private String seatNumber; // Denormalized for performance
    
    @NotNull
    private List<OrderItem> items;
    
    @NotNull
    private BigDecimal totalAmount;
    
    @NotNull
    private OrderStatus status;
    
    private String notes;
    
    private LocalDateTime requestedDeliveryTime;
    
    @NotNull
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime confirmedAt;
    
    private LocalDateTime deliveredAt;
    
    // Sync-related fields
    private boolean syncedWithCrs;
    
    private LocalDateTime lastSyncAttempt;
    
    private String crsOrderId; // External CRS order identifier
    
    private int syncVersion; // For optimistic locking during sync
    
    // Constructors
    public Order() {
        this.orderId = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.status = OrderStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.syncedWithCrs = false;
        this.syncVersion = 1;
    }
    
    public Order(UUID passengerId, String seatNumber) {
        this();
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
    }
    
    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }
    
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
    
    public UUID getPassengerId() {
        return passengerId;
    }
    
    public void setPassengerId(UUID passengerId) {
        this.passengerId = passengerId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = new ArrayList<>(items);
        recalculateTotalAmount();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        
        if (status == OrderStatus.CONFIRMED && confirmedAt == null) {
            this.confirmedAt = LocalDateTime.now();
        } else if (status == OrderStatus.DELIVERED && deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getRequestedDeliveryTime() {
        return requestedDeliveryTime;
    }
    
    public void setRequestedDeliveryTime(LocalDateTime requestedDeliveryTime) {
        this.requestedDeliveryTime = requestedDeliveryTime;
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
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public boolean isSyncedWithCrs() {
        return syncedWithCrs;
    }
    
    public void setSyncedWithCrs(boolean syncedWithCrs) {
        this.syncedWithCrs = syncedWithCrs;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getLastSyncAttempt() {
        return lastSyncAttempt;
    }
    
    public void setLastSyncAttempt(LocalDateTime lastSyncAttempt) {
        this.lastSyncAttempt = lastSyncAttempt;
    }
    
    public String getCrsOrderId() {
        return crsOrderId;
    }
    
    public void setCrsOrderId(String crsOrderId) {
        this.crsOrderId = crsOrderId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getSyncVersion() {
        return syncVersion;
    }
    
    public void setSyncVersion(int syncVersion) {
        this.syncVersion = syncVersion;
    }
    
    // Business methods
    public void addItem(OrderItem item) {
        this.items.add(item);
        recalculateTotalAmount();
        this.updatedAt = LocalDateTime.now();
        markAsModified();
    }
    
    public void removeItem(UUID orderItemId) {
        this.items.removeIf(item -> item.getOrderItemId().equals(orderItemId));
        recalculateTotalAmount();
        this.updatedAt = LocalDateTime.now();
        markAsModified();
    }
    
    public void updateItem(OrderItem updatedItem) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getOrderItemId().equals(updatedItem.getOrderItemId())) {
                items.set(i, updatedItem);
                break;
            }
        }
        recalculateTotalAmount();
        this.updatedAt = LocalDateTime.now();
        markAsModified();
    }
    
    public OrderItem findItem(UUID orderItemId) {
        return items.stream()
                .filter(item -> item.getOrderItemId().equals(orderItemId))
                .findFirst()
                .orElse(null);
    }
    
    public void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    public boolean isModifiable() {
        return status == OrderStatus.DRAFT || status == OrderStatus.PENDING;
    }
    
    public boolean isCancellable() {
        return status == OrderStatus.DRAFT || status == OrderStatus.PENDING || 
               status == OrderStatus.CONFIRMED;
    }
    
    public boolean needsSync() {
        return !syncedWithCrs && (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED);
    }
    
    public void markAsModified() {
        if (syncedWithCrs) {
            this.syncedWithCrs = false;
            this.syncVersion++;
        }
    }
    
    public void markAsSynced(String crsOrderId) {
        this.syncedWithCrs = true;
        this.crsOrderId = crsOrderId;
        this.lastSyncAttempt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markSyncFailed() {
        this.lastSyncAttempt = LocalDateTime.now();
    }
    
    public void submit() {
        if (status == OrderStatus.DRAFT && !isEmpty()) {
            setStatus(OrderStatus.PENDING);
            markAsModified();
        }
    }
    
    public void confirm() {
        if (status == OrderStatus.PENDING) {
            setStatus(OrderStatus.CONFIRMED);
        }
    }
    
    public void cancel() {
        if (isCancellable()) {
            setStatus(OrderStatus.CANCELLED);
            markAsModified();
        }
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", passengerId=" + passengerId +
                ", seatNumber='" + seatNumber + '\'' +
                ", itemCount=" + items.size() +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", syncedWithCrs=" + syncedWithCrs +
                '}';
    }
}

