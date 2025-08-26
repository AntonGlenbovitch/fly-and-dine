package com.airline.ordering.repository.impl;

import com.airline.ordering.domain.Order;
import com.airline.ordering.domain.OrderItem;
import com.airline.ordering.domain.OrderItemStatus;
import com.airline.ordering.domain.OrderStatus;
import com.airline.ordering.repository.OrderRepository;
import com.airline.ordering.repository.SQLiteConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

public class OrderRepositoryImpl implements OrderRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);
    private final ObjectMapper objectMapper;
    
    public OrderRepositoryImpl() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public Order save(Order order) {
        String sqlOrder = "INSERT INTO orders(order_id, passenger_id, seat_number, total_amount, status, notes, requested_delivery_time, created_at, updated_at, confirmed_at, delivered_at, synced_with_crs, last_sync_attempt, crs_order_id, sync_version) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                          + " ON CONFLICT(order_id) DO UPDATE SET passenger_id=?, seat_number=?, total_amount=?, status=?, notes=?, requested_delivery_time=?, updated_at=?, confirmed_at=?, delivered_at=?, synced_with_crs=?, last_sync_attempt=?, crs_order_id=?, sync_version=?";
        
        String sqlOrderItemDelete = "DELETE FROM order_items WHERE order_id = ?";
        String sqlOrderItemInsert = "INSERT INTO order_items(order_item_id, order_id, menu_item_id, menu_item_name, quantity, unit_price, total_price, special_instructions, substituted_from_item_id, status, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        
        try (Connection conn = SQLiteConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            // Save Order
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder)) {
                pstmt.setString(1, order.getOrderId().toString());
                pstmt.setString(2, order.getPassengerId().toString());
                pstmt.setString(3, order.getSeatNumber());
                pstmt.setDouble(4, order.getTotalAmount().doubleValue());
                pstmt.setString(5, order.getStatus().name());
                pstmt.setString(6, order.getNotes());
                pstmt.setString(7, order.getRequestedDeliveryTime() != null ? order.getRequestedDeliveryTime().toString() : null);
                pstmt.setString(8, order.getCreatedAt().toString());
                pstmt.setString(9, LocalDateTime.now().toString());
                pstmt.setString(10, order.getConfirmedAt() != null ? order.getConfirmedAt().toString() : null);
                pstmt.setString(11, order.getDeliveredAt() != null ? order.getDeliveredAt().toString() : null);
                pstmt.setInt(12, order.isSyncedWithCrs() ? 1 : 0);
                pstmt.setString(13, order.getLastSyncAttempt() != null ? order.getLastSyncAttempt().toString() : null);
                pstmt.setString(14, order.getCrsOrderId());
                pstmt.setInt(15, order.getSyncVersion());
                
                // For ON CONFLICT UPDATE part
                pstmt.setString(16, order.getPassengerId().toString());
                pstmt.setString(17, order.getSeatNumber());
                pstmt.setDouble(18, order.getTotalAmount().doubleValue());
                pstmt.setString(19, order.getStatus().name());
                pstmt.setString(20, order.getNotes());
                pstmt.setString(21, order.getRequestedDeliveryTime() != null ? order.getRequestedDeliveryTime().toString() : null);
                pstmt.setString(22, LocalDateTime.now().toString());
                pstmt.setString(23, order.getConfirmedAt() != null ? order.getConfirmedAt().toString() : null);
                pstmt.setString(24, order.getDeliveredAt() != null ? order.getDeliveredAt().toString() : null);
                pstmt.setInt(25, order.isSyncedWithCrs() ? 1 : 0);
                pstmt.setString(26, order.getLastSyncAttempt() != null ? order.getLastSyncAttempt().toString() : null);
                pstmt.setString(27, order.getCrsOrderId());
                pstmt.setInt(28, order.getSyncVersion());
                
                pstmt.executeUpdate();
            }
            
            // Delete existing OrderItems for this order
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrderItemDelete)) {
                pstmt.setString(1, order.getOrderId().toString());
                pstmt.executeUpdate();
            }
            
            // Insert new OrderItems
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrderItemInsert)) {
                for (OrderItem item : order.getItems()) {
                    pstmt.setString(1, item.getOrderItemId().toString());
                    pstmt.setString(2, order.getOrderId().toString());
                    pstmt.setString(3, item.getMenuItemId().toString());
                    pstmt.setString(4, item.getMenuItemName());
                    pstmt.setInt(5, item.getQuantity());
                    pstmt.setDouble(6, item.getUnitPrice().doubleValue());
                    pstmt.setDouble(7, item.getTotalPrice().doubleValue());
                    pstmt.setString(8, item.getSpecialInstructions());
                    pstmt.setString(9, item.getSubstitutedFromItemId() != null ? item.getSubstitutedFromItemId().toString() : null);
                    pstmt.setString(10, item.getStatus().name());
                    pstmt.setString(11, item.getCreatedAt().toString());
                    pstmt.setString(12, LocalDateTime.now().toString());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            conn.commit(); // Commit transaction
            logger.info("Order saved: {}", order.getOrderId());
            return order;
        } catch (SQLException e) {
            logger.error("Error saving order {}: {}", order.getOrderId(), e.getMessage());
            throw new RuntimeException("Error saving order", e);
        }
    }
    
    @Override
    public Optional<Order> findById(UUID id) {
        String sqlOrder = "SELECT * FROM orders WHERE order_id = ?";
        String sqlOrderItems = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (Connection conn = SQLiteConnection.getConnection()) {
            Order order = null;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder)) {
                pstmt.setString(1, id.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    order = mapRowToOrder(rs);
                }
            }
            
            if (order != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sqlOrderItems)) {
                    pstmt.setString(1, id.toString());
                    ResultSet rs = pstmt.executeQuery();
                    List<OrderItem> items = new ArrayList<>();
                    while (rs.next()) {
                        items.add(mapRowToOrderItem(rs));
                    }
                    order.setItems(items);
                }
                return Optional.of(order);
            }
        } catch (SQLException e) {
            logger.error("Error finding order by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error finding order by ID", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all orders: {}", e.getMessage());
            throw new RuntimeException("Error finding all orders", e);
        }
        // For each order, fetch its items
        for (Order order : orders) {
            loadOrderItems(order);
        }
        return orders;
    }
    
    @Override
    public void deleteById(UUID id) {
        String sqlOrder = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlOrder)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
            logger.info("Order deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting order by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting order by ID", e);
        }
    }
    
    @Override
    public void delete(Order entity) {
        deleteById(entity.getOrderId());
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting orders: {}", e.getMessage());
            throw new RuntimeException("Error counting orders", e);
        }
        return 0;
    }
    
    @Override
    public List<Order> findByPassengerId(UUID passengerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE passenger_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passengerId.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by passenger ID {}: {}", passengerId, e.getMessage());
            throw new RuntimeException("Error finding orders by passenger ID", e);
        }
        // For each order, fetch its items
        for (Order order : orders) {
            loadOrderItems(order);
        }
        return orders;
    }
    
    @Override
    public List<Order> findBySeatNumber(String seatNumber) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE seat_number = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, seatNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by seat number {}: {}", seatNumber, e.getMessage());
            throw new RuntimeException("Error finding orders by seat number", e);
        }
        // For each order, fetch its items
        for (Order order : orders) {
            loadOrderItems(order);
        }
        return orders;
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by status {}: {}", status, e.getMessage());
            throw new RuntimeException("Error finding orders by status", e);
        }
        // For each order, fetch its items
        for (Order order : orders) {
            loadOrderItems(order);
        }
        return orders;
    }
    
    @Override
    public List<Order> findOrdersRequiringSync() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE synced_with_crs = 0 AND (status = ? OR status = ? OR status = ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, OrderStatus.PENDING.name());
            pstmt.setString(2, OrderStatus.CONFIRMED.name());
            pstmt.setString(3, OrderStatus.CANCELLED.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding orders requiring sync: {}", e.getMessage());
            throw new RuntimeException("Error finding orders requiring sync", e);
        }
        // For each order, fetch its items
        for (Order order : orders) {
            loadOrderItems(order);
        }
        return orders;
    }
    
    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(UUID.fromString(rs.getString("order_id")));
        order.setPassengerId(UUID.fromString(rs.getString("passenger_id")));
        order.setSeatNumber(rs.getString("seat_number"));
        order.setTotalAmount(BigDecimal.valueOf(rs.getDouble("total_amount")));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setNotes(rs.getString("notes"));
        order.setRequestedDeliveryTime(rs.getString("requested_delivery_time") != null && !rs.getString("requested_delivery_time").isEmpty() ? LocalDateTime.parse(rs.getString("requested_delivery_time")) : null);
        order.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
        order.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at")));
        order.setConfirmedAt(rs.getString("confirmed_at") != null && !rs.getString("confirmed_at").isEmpty() ? LocalDateTime.parse(rs.getString("confirmed_at")) : null);
        order.setDeliveredAt(rs.getString("delivered_at") != null && !rs.getString("delivered_at").isEmpty() ? LocalDateTime.parse(rs.getString("delivered_at")) : null);
        order.setSyncedWithCrs(rs.getInt("synced_with_crs") == 1);
        order.setLastSyncAttempt(rs.getString("last_sync_attempt") != null && !rs.getString("last_sync_attempt").isEmpty() ? LocalDateTime.parse(rs.getString("last_sync_attempt")) : null);
        order.setCrsOrderId(rs.getString("crs_order_id"));
        order.setSyncVersion(rs.getInt("sync_version"));
        return order;
    }
    
    private OrderItem mapRowToOrderItem(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(UUID.fromString(rs.getString("order_item_id")));
        orderItem.setMenuItemId(UUID.fromString(rs.getString("menu_item_id")));
        orderItem.setMenuItemName(rs.getString("menu_item_name"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setUnitPrice(BigDecimal.valueOf(rs.getDouble("unit_price")));
        orderItem.setTotalPrice(BigDecimal.valueOf(rs.getDouble("total_price")));
        orderItem.setSpecialInstructions(rs.getString("special_instructions"));
        orderItem.setSubstitutedFromItemId(rs.getString("substituted_from_item_id") != null ? UUID.fromString(rs.getString("substituted_from_item_id")) : null);
        orderItem.setStatus(OrderItemStatus.valueOf(rs.getString("status")));
        orderItem.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
        orderItem.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at")));
        return orderItem;
    }
}
