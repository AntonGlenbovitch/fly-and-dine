package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.util.List;

/**
 * Interface for resolving conflicts between local and remote order data.
 * Provides methods for detecting, analyzing, and resolving data conflicts.
 */
public interface ConflictResolver {
    
    /**
     * Detects conflicts between local and remote versions of an order.
     * 
     * @param localOrder the local version of the order
     * @param remoteOrder the remote version of the order
     * @return ConflictInfo if a conflict is detected, null otherwise
     */
    ConflictInfo detectConflict(Order localOrder, Order remoteOrder);
    
    /**
     * Detects conflicts for a batch of orders.
     * 
     * @param localOrders the local versions of the orders
     * @param remoteOrders the remote versions of the orders
     * @return list of detected conflicts
     */
    List<ConflictInfo> detectConflicts(List<Order> localOrders, List<Order> remoteOrders);
    
    /**
     * Resolves a single conflict using the specified strategy.
     * 
     * @param conflict the conflict to resolve
     * @param strategy the resolution strategy to use
     * @return ConflictResolution containing the resolved order
     */
    ConflictResolution resolveConflict(ConflictInfo conflict, ConflictResolutionStrategy strategy);
    
    /**
     * Resolves multiple conflicts using the specified strategy.
     * 
     * @param conflicts the conflicts to resolve
     * @param strategy the resolution strategy to use
     * @return ConflictResolutionResult containing all resolution outcomes
     */
    ConflictResolutionResult resolveConflicts(List<ConflictInfo> conflicts, ConflictResolutionStrategy strategy);
    
    /**
     * Automatically resolves conflicts that can be safely resolved without user intervention.
     * 
     * @param conflicts the conflicts to auto-resolve
     * @return ConflictResolutionResult containing auto-resolution outcomes
     */
    ConflictResolutionResult autoResolveConflicts(List<ConflictInfo> conflicts);
    
    /**
     * Merges two orders intelligently, combining non-conflicting changes.
     * 
     * @param localOrder the local version of the order
     * @param remoteOrder the remote version of the order
     * @return the merged order, or null if merging is not possible
     */
    Order mergeOrders(Order localOrder, Order remoteOrder);
    
    /**
     * Determines the winning order based on priority rules.
     * 
     * @param localOrder the local version of the order
     * @param remoteOrder the remote version of the order
     * @return the order with higher priority
     */
    Order selectByPriority(Order localOrder, Order remoteOrder);
    
    /**
     * Determines the winning order based on timestamps (last write wins).
     * 
     * @param localOrder the local version of the order
     * @param remoteOrder the remote version of the order
     * @return the order with the most recent timestamp
     */
    Order selectByTimestamp(Order localOrder, Order remoteOrder);
    
    /**
     * Validates that a resolved order is consistent and valid.
     * 
     * @param resolvedOrder the resolved order to validate
     * @param originalConflict the original conflict information
     * @return true if the resolution is valid, false otherwise
     */
    boolean validateResolution(Order resolvedOrder, ConflictInfo originalConflict);
    
    /**
     * Gets the recommended resolution strategy for a specific conflict.
     * 
     * @param conflict the conflict to analyze
     * @return the recommended resolution strategy
     */
    ConflictResolutionStrategy getRecommendedStrategy(ConflictInfo conflict);
    
    /**
     * Checks if a conflict can be automatically resolved.
     * 
     * @param conflict the conflict to check
     * @return true if the conflict can be auto-resolved, false otherwise
     */
    boolean canAutoResolve(ConflictInfo conflict);
    
    /**
     * Creates a backup of the original orders before resolution.
     * 
     * @param conflicts the conflicts being resolved
     * @return true if backup is successful, false otherwise
     */
    boolean createBackup(List<ConflictInfo> conflicts);
    
    /**
     * Restores orders from backup if resolution fails.
     * 
     * @param backupId the ID of the backup to restore
     * @return true if restore is successful, false otherwise
     */
    boolean restoreFromBackup(String backupId);
}

