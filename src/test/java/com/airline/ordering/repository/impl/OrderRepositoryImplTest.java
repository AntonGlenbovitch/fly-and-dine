package com.airline.ordering.repository.impl;

import com.airline.ordering.domain.Order;
import com.airline.ordering.domain.OrderItem;
import com.airline.ordering.domain.OrderItemStatus;
import com.airline.ordering.domain.OrderStatus;
import com.airline.ordering.domain.Passenger;
import com.airline.ordering.domain.PassengerType;
import com.airline.ordering.repository.SQLiteConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryImplTest {
    
    private OrderRepositoryImpl orderRepository;
    private PassengerRepositoryImpl passengerRepository;
    private static final String DB_FILE = "inflight_ordering.db";
    
    @BeforeEach
    void setUp() {
        // Ensure a clean database for each test
        File dbFile = new File(DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        SQLiteConnection.initializeDatabase();
        orderRepository = new OrderRepositoryImpl();
        passengerRepository = new PassengerRepositoryImpl();
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
        Passenger passenger = new Passenger("Test", "User", "test@example.com", "10B", PassengerType.ECONOMY);
        passengerRepository.save(passenger);
        
        Order order = new Order(passenger.getPassengerId(), passenger.getSeatNumber());
        OrderItem item1 = new OrderItem(UUID.randomUUID(), "Coffee", 1, new BigDecimal("3.00"));
        OrderItem item2 = new OrderItem(UUID.randomUUID(), "Sandwich", 1, new BigDecimal("12.00"));
        order.addItem(item1);
        order.addItem(item2);
        order.submit();
        
        Order savedOrder = orderRepository.save(order);
        
        assertNotNull(savedOrder);
        assertEquals(order.getOrderId(), savedOrder.getOrderId());
        
        Optional<Order> foundOrder = orderRepository.findById(order.getOrderId());
        assertTrue(foundOrder.isPresent());
        assertEquals(order.getTotalAmount().doubleValue(), foundOrder.get().getTotalAmount().doubleValue());
        assertEquals(2, foundOrder.get().getItems().size());
        assertTrue(foundOrder.get().getItems().stream().anyMatch(oi -> oi.getMenuItemName().equals("Coffee")));
    }
    
    @Test
    void testUpdateOrder() {
        Passenger passenger = new Passenger("Test", "User", "test@example.com", "10B", PassengerType.ECONOMY);
        passengerRepository.save(passenger);
        
        Order order = new Order(passenger.getPassengerId(), passenger.getSeatNumber());
        OrderItem item1 = new OrderItem(UUID.randomUUID(), "Coffee", 1, new BigDecimal("3.00"));
        order.addItem(item1);
        orderRepository.save(order);
        
        order.setStatus(OrderStatus.CONFIRMED);
        OrderItem item2 = new OrderItem(UUID.randomUUID(), "Sandwich", 1, new BigDecimal("12.00"));
        order.addItem(item2);
        order.setNotes("Urgent delivery");
        orderRepository.save(order);
        
        Optional<Order> updatedOrder = orderRepository.findById(order.getOrderId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.get().getStatus());
        assertEquals("Urgent delivery", updatedOrder.get().getNotes());
        assertEquals(2, updatedOrder.get().getItems().size());
        assertTrue(updatedOrder.get().getItems().stream().anyMatch(oi -> oi.getMenuItemName().equals("Sandwich")));
    }
    
    @Test
    void testFindAll() {
        Passenger p1 = new Passenger("Alice", "Brown", "alice@example.com", "3C", PassengerType.FIRST_CLASS);
        passengerRepository.save(p1);
        
        Order order1 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order1.addItem(new OrderItem(UUID.randomUUID(), "Drink", 1, new BigDecimal("5.00")));
        orderRepository.save(order1);
        
        Passenger p2 = new Passenger("Bob", "White", "bob@example.com", "4D", PassengerType.CREW);
        passengerRepository.save(p2);
        
        Order order2 = new Order(p2.getPassengerId(), p2.getSeatNumber());
        order2.addItem(new OrderItem(UUID.randomUUID(), "Meal", 1, new BigDecimal("15.00")));
        orderRepository.save(order2);
        
        List<Order> orders = orderRepository.findAll();
        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getPassengerId().equals(p1.getPassengerId())));
        assertTrue(orders.stream().anyMatch(o -> o.getPassengerId().equals(p2.getPassengerId())));
    }
    
    @Test
    void testDeleteById() {
        Passenger passenger = new Passenger("Charlie", "Green", "charlie@example.com", "5E", PassengerType.PREMIUM_ECONOMY);
        passengerRepository.save(passenger);
        
        Order order = new Order(passenger.getPassengerId(), passenger.getSeatNumber());
        order.addItem(new OrderItem(UUID.randomUUID(), "Snack", 1, new BigDecimal("4.00")));
        orderRepository.save(order);
        
        orderRepository.deleteById(order.getOrderId());
        
        Optional<Order> foundOrder = orderRepository.findById(order.getOrderId());
        assertFalse(foundOrder.isPresent());
    }
    
    @Test
    void testCount() {
        assertEquals(0, orderRepository.count());
        
        Passenger p1 = new Passenger("David", "Black", "david@example.com", "6F", PassengerType.ECONOMY);
        passengerRepository.save(p1);
        orderRepository.save(new Order(p1.getPassengerId(), p1.getSeatNumber()));
        assertEquals(1, orderRepository.count());
        
        Passenger p2 = new Passenger("Eve", "Gray", "eve@example.com", "7G", PassengerType.BUSINESS);
        passengerRepository.save(p2);
        orderRepository.save(new Order(p2.getPassengerId(), p2.getSeatNumber()));
        assertEquals(2, orderRepository.count());
    }
    
    @Test
    void testFindByPassengerId() {
        Passenger p1 = new Passenger("Frank", "Blue", "frank@example.com", "8H", PassengerType.CREW);
        Passenger p2 = new Passenger("Grace", "Red", "grace@example.com", "9I", PassengerType.ECONOMY);
        passengerRepository.save(p1);
        passengerRepository.save(p2);
        
        Order order1 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order1.addItem(new OrderItem(UUID.randomUUID(), "Water", 1, new BigDecimal("2.00")));
        orderRepository.save(order1);
        
        Order order2 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order2.addItem(new OrderItem(UUID.randomUUID(), "Soda", 1, new BigDecimal("3.00")));
        orderRepository.save(order2);
        
        Order order3 = new Order(p2.getPassengerId(), p2.getSeatNumber());
        order3.addItem(new OrderItem(UUID.randomUUID(), "Juice", 1, new BigDecimal("4.00")));
        orderRepository.save(order3);
        
        List<Order> p1Orders = orderRepository.findByPassengerId(p1.getPassengerId());
        assertEquals(2, p1Orders.size());
        assertTrue(p1Orders.stream().anyMatch(o -> o.getOrderId().equals(order1.getOrderId())));
        assertTrue(p1Orders.stream().anyMatch(o -> o.getOrderId().equals(order2.getOrderId())));
        
        List<Order> p2Orders = orderRepository.findByPassengerId(p2.getPassengerId());
        assertEquals(1, p2Orders.size());
        assertTrue(p2Orders.stream().anyMatch(o -> o.getOrderId().equals(order3.getOrderId())));
    }
    
    @Test
    void testFindBySeatNumber() {
        Passenger p1 = new Passenger("Henry", "White", "henry@example.com", "11J", PassengerType.BUSINESS);
        passengerRepository.save(p1);
        
        Order order1 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order1.addItem(new OrderItem(UUID.randomUUID(), "Wine", 1, new BigDecimal("10.00")));
        orderRepository.save(order1);
        
        Order order2 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order2.addItem(new OrderItem(UUID.randomUUID(), "Beer", 1, new BigDecimal("8.00")));
        orderRepository.save(order2);
        
        List<Order> seatOrders = orderRepository.findBySeatNumber("11J");
        assertEquals(2, seatOrders.size());
        assertTrue(seatOrders.stream().anyMatch(o -> o.getOrderId().equals(order1.getOrderId())));
        assertTrue(seatOrders.stream().anyMatch(o -> o.getOrderId().equals(order2.getOrderId())));
    }
    
    @Test
    void testFindByStatus() {
        Passenger p1 = new Passenger("Ivy", "Green", "ivy@example.com", "12K", PassengerType.FIRST_CLASS);
        passengerRepository.save(p1);
        
        Order order1 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order1.addItem(new OrderItem(UUID.randomUUID(), "Dessert", 1, new BigDecimal("6.00")));
        order1.submit();
        orderRepository.save(order1);
        
        Order order2 = new Order(p1.getPassengerId(), p1.getSeatNumber());
        order2.addItem(new OrderItem(UUID.randomUUID(), "Appetizer", 1, new BigDecimal("7.00")));
        order2.submit();
        order2.confirm();
        orderRepository.save(order2);
        
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        assertEquals(1, pendingOrders.size());
        assertEquals(order1.getOrderId(), pendingOrders.get(0).getOrderId());
        
        List<Order> confirmedOrders = orderRepository.findByStatus(OrderStatus.CONFIRMED);
        assertEquals(1, confirmedOrders.size());
        assertEquals(order2.getOrderId(), confirmedOrders.get(0).getOrderId());
    }
    
    @Test
    void testFindOrdersRequiringSync() {
        Passenger p1 = new Passenger("Jack", "Brown", "jack@example.com", "13L", PassengerType.CREW);
        passengerRepository.save(p1);
        
        Order order1 = new Order(p1.getPassengerId(), p1.getSeatNumber()); // DRAFT, not synced
        order1.addItem(new OrderItem(UUID.randomUUID(), "Item A", 1, new BigDecimal("1.00")));
        orderRepository.save(order1);
        
        Order order2 = new Order(p1.getPassengerId(), p1.getSeatNumber()); // PENDING, needs sync
        order2.addItem(new OrderItem(UUID.randomUUID(), "Item B", 1, new BigDecimal("2.00")));
        order2.submit();
        orderRepository.save(order2);
        
        Order order3 = new Order(p1.getPassengerId(), p1.getSeatNumber()); // CONFIRMED, needs sync
        order3.addItem(new OrderItem(UUID.randomUUID(), "Item C", 1, new BigDecimal("3.00")));
        order3.submit();
        order3.confirm();
        orderRepository.save(order3);
        
        Order order4 = new Order(p1.getPassengerId(), p1.getSeatNumber()); // DELIVERED, synced
        order4.addItem(new OrderItem(UUID.randomUUID(), "Item D", 1, new BigDecimal("4.00")));
        order4.submit();
        order4.confirm();
        order4.setStatus(OrderStatus.DELIVERED);
        order4.markAsSynced("CRS-SYNCED-1");
        orderRepository.save(order4);
        
        Order order5 = new Order(p1.getPassengerId(), p1.getSeatNumber()); // CANCELLED, needs sync
        order5.addItem(new OrderItem(UUID.randomUUID(), "Item E", 1, new BigDecimal("5.00")));
        order5.submit();
        order5.cancel();
        orderRepository.save(order5);
        
        List<Order> ordersToSync = orderRepository.findOrdersRequiringSync();
        assertEquals(3, ordersToSync.size());
        assertTrue(ordersToSync.stream().anyMatch(o -> o.getOrderId().equals(order2.getOrderId())));
        assertTrue(ordersToSync.stream().anyMatch(o -> o.getOrderId().equals(order3.getOrderId())));
        assertTrue(ordersToSync.stream().anyMatch(o -> o.getOrderId().equals(order5.getOrderId())));
        assertFalse(ordersToSync.stream().anyMatch(o -> o.getOrderId().equals(order1.getOrderId())));
        assertFalse(ordersToSync.stream().anyMatch(o -> o.getOrderId().equals(order4.getOrderId())));
    }
}


