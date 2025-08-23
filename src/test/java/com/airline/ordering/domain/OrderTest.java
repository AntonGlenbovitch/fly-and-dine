package com.airline.ordering.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Order domain entity.
 */
class OrderTest {
    
    private Order order;
    private UUID passengerId;
    private String seatNumber;
    
    @BeforeEach
    void setUp() {
        passengerId = UUID.randomUUID();
        seatNumber = "12A";
        order = new Order(passengerId, seatNumber);
    }
    
    @Test
    void testOrderCreation() {
        assertNotNull(order.getOrderId());
        assertEquals(passengerId, order.getPassengerId());
        assertEquals(seatNumber, order.getSeatNumber());
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertTrue(order.getItems().isEmpty());
        assertFalse(order.isSyncedWithCrs());
        assertEquals(1, order.getSyncVersion());
    }
    
    @Test
    void testAddItem() {
        OrderItem item = createTestOrderItem("Chicken Sandwich", new BigDecimal("15.99"), 1);
        
        order.addItem(item);
        
        assertEquals(1, order.getItemCount());
        assertEquals(new BigDecimal("15.99"), order.getTotalAmount());
        assertFalse(order.isSyncedWithCrs());
        assertEquals(1, order.getSyncVersion()); // Version doesn't increment until synced first
    }
    
    @Test
    void testAddMultipleItems() {
        OrderItem item1 = createTestOrderItem("Chicken Sandwich", new BigDecimal("15.99"), 1);
        OrderItem item2 = createTestOrderItem("Coffee", new BigDecimal("4.50"), 2);
        
        order.addItem(item1);
        order.addItem(item2);
        
        assertEquals(2, order.getItemCount());
        assertEquals(3, order.getTotalQuantity());
        assertEquals(new BigDecimal("24.99"), order.getTotalAmount()); // 15.99 + (4.50 * 2) = 24.99
    }
    
    @Test
    void testRemoveItem() {
        OrderItem item1 = createTestOrderItem("Chicken Sandwich", new BigDecimal("15.99"), 1);
        OrderItem item2 = createTestOrderItem("Coffee", new BigDecimal("4.50"), 1);
        
        order.addItem(item1);
        order.addItem(item2);
        order.removeItem(item1.getOrderItemId());
        
        assertEquals(1, order.getItemCount());
        assertEquals(new BigDecimal("4.50"), order.getTotalAmount());
        assertNull(order.findItem(item1.getOrderItemId()));
        assertNotNull(order.findItem(item2.getOrderItemId()));
    }
    
    @Test
    void testUpdateItem() {
        OrderItem item = createTestOrderItem("Chicken Sandwich", new BigDecimal("15.99"), 1);
        order.addItem(item);
        
        item.setQuantity(3);
        order.updateItem(item);
        
        assertEquals(new BigDecimal("47.97"), order.getTotalAmount());
        assertEquals(3, order.getTotalQuantity());
    }
    
    @Test
    void testOrderStatusTransitions() {
        assertTrue(order.isModifiable());
        assertTrue(order.isCancellable());
        
        // Add an item first so order can be submitted
        OrderItem item = createTestOrderItem("Test Item", new BigDecimal("10.00"), 1);
        order.addItem(item);
        
        order.submit();
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertTrue(order.isModifiable());
        assertTrue(order.isCancellable());
        assertTrue(order.needsSync());
        
        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotNull(order.getConfirmedAt());
        assertFalse(order.isModifiable());
        assertTrue(order.isCancellable());
        
        order.setStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertNotNull(order.getDeliveredAt());
        assertFalse(order.isModifiable());
        assertFalse(order.isCancellable());
    }
    
    @Test
    void testCannotSubmitEmptyOrder() {
        order.submit();
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }
    
    @Test
    void testSyncOperations() {
        // Add an item first so order can be submitted
        OrderItem item = createTestOrderItem("Test Item", new BigDecimal("10.00"), 1);
        order.addItem(item);
        
        order.submit();
        assertTrue(order.needsSync());
        
        String crsOrderId = "CRS-12345";
        order.markAsSynced(crsOrderId);
        
        assertTrue(order.isSyncedWithCrs());
        assertEquals(crsOrderId, order.getCrsOrderId());
        assertNotNull(order.getLastSyncAttempt());
        assertFalse(order.needsSync());
    }
    
    @Test
    void testMarkAsModifiedResetsSync() {
        order.markAsSynced("CRS-12345");
        assertTrue(order.isSyncedWithCrs());
        int originalVersion = order.getSyncVersion();
        
        order.markAsModified();
        
        assertFalse(order.isSyncedWithCrs());
        assertEquals(originalVersion + 1, order.getSyncVersion());
    }
    
    @Test
    void testSyncFailure() {
        order.markSyncFailed();
        assertNotNull(order.getLastSyncAttempt());
        assertFalse(order.isSyncedWithCrs());
    }
    
    @Test
    void testOrderEquality() {
        Order order2 = new Order(UUID.randomUUID(), "12B");
        
        assertNotEquals(order, order2);
        assertEquals(order, order);
        assertEquals(order.hashCode(), order.hashCode());
    }
    
    @Test
    void testRecalculateTotalAmount() {
        OrderItem item1 = createTestOrderItem("Item 1", new BigDecimal("10.00"), 2);
        OrderItem item2 = createTestOrderItem("Item 2", new BigDecimal("5.00"), 3);
        
        order.addItem(item1);
        order.addItem(item2);
        
        // Manually change item price and recalculate
        item1.setUnitPrice(new BigDecimal("12.00"));
        order.recalculateTotalAmount();
        
        assertEquals(new BigDecimal("39.00"), order.getTotalAmount()); // (12*2) + (5*3)
    }
    
    private OrderItem createTestOrderItem(String name, BigDecimal price, int quantity) {
        return new OrderItem(UUID.randomUUID(), name, quantity, price);
    }
}

