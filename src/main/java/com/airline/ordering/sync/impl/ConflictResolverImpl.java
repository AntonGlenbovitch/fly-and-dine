package com.airline.ordering.sync.impl;

import com.airline.ordering.domain.Order;
import com.airline.ordering.domain.OrderItem;
import com.airline.ordering.domain.OrderStatus;
import com.airline.ordering.sync.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ConflictResolver for handling data conflicts between local and remote orders.
 */
public class ConflictResolverImpl implements ConflictResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(ConflictResolverImpl.class);
    
    @Override
    public ConflictInfo detectConflict(Order localOrder, Order remoteOrder) {
        if (localOrder == null || remoteOrder == null) {
            return null;
        }
        
        if (!localOrder.getOrderId().equals(remoteOrder.getOrderId())) {
            throw new IllegalArgumentException("Orders must have the same ID for conflict detection");
        }
        
        // Check for status conflicts
        if (!localOrder.getStatus().equals(remoteOrder.getStatus())) {
            String description = String.format("Status conflict: local=%s, remote=%s", 
                                              localOrder.getStatus(), remoteOrder.getStatus());
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.STATUS_CONFLICT, description);
        }
        
        // Check for content conflicts (items, quantities, prices)
        if (hasContentConflict(localOrder, remoteOrder)) {
            String description = "Order content differs between local and remote versions";
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.CONTENT_CONFLICT, description);
        }
        
        // Check for timestamp conflicts
        if (hasTimestampConflict(localOrder, remoteOrder)) {
            String description = "Modification timestamps indicate concurrent updates";
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.TIMESTAMP_CONFLICT, description);
        }
        
        // Check for deletion conflicts
        if (hasDeletionConflict(localOrder, remoteOrder)) {
            String description = "Order deleted remotely but modified locally";
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.DELETION_CONFLICT, description);
        }
        
        // Check for confirmation conflicts
        if (hasConfirmationConflict(localOrder, remoteOrder)) {
            String description = "Order confirmation status conflicts";
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.CONFIRMATION_CONFLICT, description);
        }
        
        // Check for passenger conflicts
        if (!localOrder.getPassengerId().equals(remoteOrder.getPassengerId())) {
            String description = "Passenger assignment differs between versions";
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.PASSENGER_CONFLICT, description);
        }
        
        // Check for seat conflicts
        if (!Objects.equals(localOrder.getSeatNumber(), remoteOrder.getSeatNumber())) {
            String description = "Seat assignment differs between versions";
            return new ConflictInfo(localOrder.getOrderId(), localOrder, remoteOrder, 
                                  ConflictType.SEAT_CONFLICT, description);
        }
        
        return null; // No conflict detected
    }
    
    @Override
    public List<ConflictInfo> detectConflicts(List<Order> localOrders, List<Order> remoteOrders) {
        List<ConflictInfo> conflicts = new ArrayList<>();
        
        Map<UUID, Order> remoteOrderMap = remoteOrders.stream()
                .collect(Collectors.toMap(Order::getOrderId, order -> order));
        
        for (Order localOrder : localOrders) {
            Order remoteOrder = remoteOrderMap.get(localOrder.getOrderId());
            if (remoteOrder != null) {
                ConflictInfo conflict = detectConflict(localOrder, remoteOrder);
                if (conflict != null) {
                    conflicts.add(conflict);
                }
            }
        }
        
        return conflicts;
    }
    
    @Override
    public ConflictResolution resolveConflict(ConflictInfo conflict, ConflictResolutionStrategy strategy) {
        UUID conflictId = UUID.randomUUID();
        Order resolvedOrder = null;
        String reason = "";
        
        try {
            switch (strategy) {
                case USE_LOCAL:
                    resolvedOrder = conflict.getLocalVersion();
                    reason = "Used local version as requested";
                    break;
                    
                case USE_REMOTE:
                    resolvedOrder = conflict.getRemoteVersion();
                    reason = "Used remote version as requested";
                    break;
                    
                case LAST_WRITE_WINS:
                    resolvedOrder = selectByTimestamp(conflict.getLocalVersion(), conflict.getRemoteVersion());
                    reason = "Selected version with most recent timestamp";
                    break;
                    
                case MERGE:
                    resolvedOrder = mergeOrders(conflict.getLocalVersion(), conflict.getRemoteVersion());
                    reason = "Merged both versions intelligently";
                    break;
                    
                case PRIORITY_BASED:
                    resolvedOrder = selectByPriority(conflict.getLocalVersion(), conflict.getRemoteVersion());
                    reason = "Selected version based on priority rules";
                    break;
                    
                case REJECT_CHANGE:
                    resolvedOrder = conflict.getRemoteVersion(); // Keep existing (remote) version
                    reason = "Rejected local changes, kept remote version";
                    break;
                    
                default:
                    throw new UnsupportedOperationException("Strategy " + strategy + " requires manual resolution");
            }
            
            if (resolvedOrder != null && validateResolution(resolvedOrder, conflict)) {
                return ConflictResolution.automatic(conflictId, strategy, resolvedOrder, reason);
            } else {
                throw new IllegalStateException("Resolution validation failed");
            }
            
        } catch (Exception e) {
            logger.error("Failed to resolve conflict for order {}: {}", conflict.getOrderId(), e.getMessage());
            throw new RuntimeException("Conflict resolution failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ConflictResolutionResult resolveConflicts(List<ConflictInfo> conflicts, ConflictResolutionStrategy strategy) {
        ConflictResolutionResult.Builder resultBuilder = ConflictResolutionResult.builder();
        
        for (ConflictInfo conflict : conflicts) {
            try {
                ConflictResolution resolution = resolveConflict(conflict, strategy);
                resultBuilder.addResolvedConflictId(conflict.getOrderId())
                           .addResolution(resolution);
            } catch (Exception e) {
                logger.error("Failed to resolve conflict for order {}: {}", conflict.getOrderId(), e.getMessage());
                resultBuilder.addUnresolvedConflictId(conflict.getOrderId())
                           .addError(SyncError.dataError(conflict.getOrderId(), 
                                   "Conflict resolution failed: " + e.getMessage(), e.toString()));
            }
        }
        
        boolean success = resultBuilder.build().getUnresolvedConflictIds().isEmpty();
        return resultBuilder.success(success)
                          .message(success ? "All conflicts resolved successfully" : "Some conflicts could not be resolved")
                          .build();
    }
    
    @Override
    public ConflictResolutionResult autoResolveConflicts(List<ConflictInfo> conflicts) {
        List<ConflictInfo> autoResolvableConflicts = conflicts.stream()
                .filter(this::canAutoResolve)
                .collect(Collectors.toList());
        
        ConflictResolutionResult.Builder resultBuilder = ConflictResolutionResult.builder();
        
        for (ConflictInfo conflict : autoResolvableConflicts) {
            try {
                ConflictResolutionStrategy strategy = getRecommendedStrategy(conflict);
                ConflictResolution resolution = resolveConflict(conflict, strategy);
                resultBuilder.addResolvedConflictId(conflict.getOrderId())
                           .addResolution(resolution);
            } catch (Exception e) {
                logger.warn("Auto-resolution failed for order {}: {}", conflict.getOrderId(), e.getMessage());
                resultBuilder.addUnresolvedConflictId(conflict.getOrderId());
            }
        }
        
        // Add conflicts that cannot be auto-resolved
        conflicts.stream()
                .filter(conflict -> !canAutoResolve(conflict))
                .forEach(conflict -> resultBuilder.addUnresolvedConflictId(conflict.getOrderId()));
        
        boolean success = !autoResolvableConflicts.isEmpty();
        String message = String.format("Auto-resolved %d of %d conflicts", 
                                      resultBuilder.build().getResolvedConflictIds().size(), 
                                      conflicts.size());
        
        return resultBuilder.success(success).message(message).build();
    }
    
    @Override
    public Order mergeOrders(Order localOrder, Order remoteOrder) {
        if (localOrder == null) return remoteOrder;
        if (remoteOrder == null) return localOrder;
        
        try {
            // Create a new order based on the more recent version
            Order baseOrder = selectByTimestamp(localOrder, remoteOrder);
            Order mergedOrder = new Order(baseOrder.getPassengerId(), baseOrder.getSeatNumber());
            
            // Copy basic properties from base order
            mergedOrder.setOrderId(baseOrder.getOrderId());
            mergedOrder.setStatus(baseOrder.getStatus());
            mergedOrder.setNotes(baseOrder.getNotes());
            
            // Merge order items intelligently
            Set<UUID> allItemIds = new HashSet<>();
            localOrder.getItems().forEach(item -> allItemIds.add(item.getMenuItemId()));
            remoteOrder.getItems().forEach(item -> allItemIds.add(item.getMenuItemId()));
            
            for (UUID itemId : allItemIds) {
                OrderItem localItem = findItemById(localOrder.getItems(), itemId);
                OrderItem remoteItem = findItemById(remoteOrder.getItems(), itemId);
                
                if (localItem != null && remoteItem != null) {
                    // Both versions have this item - use the one with higher quantity
                    OrderItem mergedItem = localItem.getQuantity() >= remoteItem.getQuantity() ? localItem : remoteItem;
                    mergedOrder.addItem(mergedItem);
                } else if (localItem != null) {
                    mergedOrder.addItem(localItem);
                } else if (remoteItem != null) {
                    mergedOrder.addItem(remoteItem);
                }
            }
            
            return mergedOrder;
            
        } catch (Exception e) {
            logger.error("Failed to merge orders: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public Order selectByPriority(Order localOrder, Order remoteOrder) {
        // Priority rules:
        // 1. Confirmed orders take precedence over pending
        // 2. Orders with payment take precedence
        // 3. Orders with more items take precedence
        // 4. Fall back to timestamp
        
        int localPriority = calculatePriority(localOrder);
        int remotePriority = calculatePriority(remoteOrder);
        
        if (localPriority > remotePriority) {
            return localOrder;
        } else if (remotePriority > localPriority) {
            return remoteOrder;
        } else {
            // Same priority, use timestamp
            return selectByTimestamp(localOrder, remoteOrder);
        }
    }
    
    @Override
    public Order selectByTimestamp(Order localOrder, Order remoteOrder) {
        LocalDateTime localTime = localOrder.getUpdatedAt();
        LocalDateTime remoteTime = remoteOrder.getUpdatedAt();
        
        if (localTime == null && remoteTime == null) {
            return localOrder; // Default to local if no timestamps
        } else if (localTime == null) {
            return remoteOrder;
        } else if (remoteTime == null) {
            return localOrder;
        } else {
            return localTime.isAfter(remoteTime) ? localOrder : remoteOrder;
        }
    }
    
    @Override
    public boolean validateResolution(Order resolvedOrder, ConflictInfo originalConflict) {
        if (resolvedOrder == null) {
            return false;
        }
        
        // Basic validation
        if (resolvedOrder.getOrderId() == null || resolvedOrder.getPassengerId() == null) {
            return false;
        }
        
        // Ensure the resolved order ID matches the original conflict
        if (!resolvedOrder.getOrderId().equals(originalConflict.getOrderId())) {
            return false;
        }
        
        // Validate order items
        if (resolvedOrder.getItems().isEmpty()) {
            logger.warn("Resolved order {} has no items", resolvedOrder.getOrderId());
            return false;
        }
        
        // Validate total amount
        if (resolvedOrder.getTotalAmount().doubleValue() < 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public ConflictResolutionStrategy getRecommendedStrategy(ConflictInfo conflict) {
        return conflict.getConflictType().getRecommendedStrategy();
    }
    
    @Override
    public boolean canAutoResolve(ConflictInfo conflict) {
        return conflict.isAutoResolvable();
    }
    
    @Override
    public boolean createBackup(List<ConflictInfo> conflicts) {
        // Implementation would save orders to backup storage
        // For now, just log the backup creation
        logger.info("Creating backup for {} conflicted orders", conflicts.size());
        return true;
    }
    
    @Override
    public boolean restoreFromBackup(String backupId) {
        // Implementation would restore orders from backup storage
        logger.info("Restoring orders from backup: {}", backupId);
        return true;
    }
    
    // Helper methods
    
    private boolean hasContentConflict(Order localOrder, Order remoteOrder) {
        // Compare total amounts
        if (!localOrder.getTotalAmount().equals(remoteOrder.getTotalAmount())) {
            return true;
        }
        
        // Compare number of items
        if (localOrder.getItems().size() != remoteOrder.getItems().size()) {
            return true;
        }
        
        // Compare individual items
        Map<UUID, OrderItem> localItemMap = localOrder.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getMenuItemId, item -> item));
        
        for (OrderItem remoteItem : remoteOrder.getItems()) {
            OrderItem localItem = localItemMap.get(remoteItem.getMenuItemId());
            if (localItem == null || !itemsEqual(localItem, remoteItem)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasTimestampConflict(Order localOrder, Order remoteOrder) {
        LocalDateTime localTime = localOrder.getUpdatedAt();
        LocalDateTime remoteTime = remoteOrder.getUpdatedAt();
        
        if (localTime == null || remoteTime == null) {
            return false;
        }
        
        // Consider it a conflict if both were modified within a short time window
        long timeDiffSeconds = Math.abs(java.time.Duration.between(localTime, remoteTime).getSeconds());
        return timeDiffSeconds < 60; // 1 minute window
    }
    
    private boolean hasDeletionConflict(Order localOrder, Order remoteOrder) {
        // This would be detected if one order is marked as deleted
        // For now, check if status indicates deletion
        return localOrder.getStatus() == OrderStatus.CANCELLED && 
               remoteOrder.getStatus() != OrderStatus.CANCELLED;
    }
    
    private boolean hasConfirmationConflict(Order localOrder, Order remoteOrder) {
        boolean localConfirmed = localOrder.getStatus() == OrderStatus.CONFIRMED || 
                                localOrder.getStatus() == OrderStatus.DELIVERED;
        boolean remoteConfirmed = remoteOrder.getStatus() == OrderStatus.CONFIRMED || 
                                 remoteOrder.getStatus() == OrderStatus.DELIVERED;
        
        return localConfirmed != remoteConfirmed;
    }
    
    private boolean itemsEqual(OrderItem item1, OrderItem item2) {
        return item1.getMenuItemId().equals(item2.getMenuItemId()) &&
               item1.getQuantity() == item2.getQuantity() &&
               item1.getUnitPrice().equals(item2.getUnitPrice());
    }
    
    private OrderItem findItemById(List<OrderItem> items, UUID itemId) {
        return items.stream()
                .filter(item -> item.getMenuItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }
    
    private int calculatePriority(Order order) {
        int priority = 0;
        
        // Status priority
        switch (order.getStatus()) {
            case CONFIRMED:
                priority += 100;
                break;
            case PENDING:
                priority += 50;
                break;
            case DRAFT:
                priority += 10;
                break;
            default:
                break;
        }
        
        // Item count priority
        priority += order.getItems().size() * 5;
        
        // Amount priority (higher amounts get slight priority)
        priority += (int) (order.getTotalAmount().doubleValue() / 10);
        
        return priority;
    }
}

