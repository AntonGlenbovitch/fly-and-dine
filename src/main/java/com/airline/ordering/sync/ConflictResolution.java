package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the resolution of a data conflict.
 */
public class ConflictResolution {
    
    private final UUID conflictId;
    private final ConflictResolutionStrategy strategy;
    private final Order resolvedOrder;
    private final String reason;
    private final LocalDateTime resolvedAt;
    private final String resolvedBy;
    private final boolean automatic;
    
    public ConflictResolution(UUID conflictId, ConflictResolutionStrategy strategy, 
                             Order resolvedOrder, String reason, String resolvedBy, boolean automatic) {
        this.conflictId = conflictId;
        this.strategy = strategy;
        this.resolvedOrder = resolvedOrder;
        this.reason = reason;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
        this.automatic = automatic;
    }
    
    // Getters
    public UUID getConflictId() {
        return conflictId;
    }
    
    public ConflictResolutionStrategy getStrategy() {
        return strategy;
    }
    
    public Order getResolvedOrder() {
        return resolvedOrder;
    }
    
    public String getReason() {
        return reason;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public String getResolvedBy() {
        return resolvedBy;
    }
    
    public boolean isAutomatic() {
        return automatic;
    }
    
    // Static factory methods
    public static ConflictResolution automatic(UUID conflictId, ConflictResolutionStrategy strategy, 
                                             Order resolvedOrder, String reason) {
        return new ConflictResolution(conflictId, strategy, resolvedOrder, reason, "SYSTEM", true);
    }
    
    public static ConflictResolution manual(UUID conflictId, ConflictResolutionStrategy strategy, 
                                          Order resolvedOrder, String reason, String userId) {
        return new ConflictResolution(conflictId, strategy, resolvedOrder, reason, userId, false);
    }
    
    public static ConflictResolution useLocal(UUID conflictId, Order localOrder, String reason) {
        return automatic(conflictId, ConflictResolutionStrategy.USE_LOCAL, localOrder, reason);
    }
    
    public static ConflictResolution useRemote(UUID conflictId, Order remoteOrder, String reason) {
        return automatic(conflictId, ConflictResolutionStrategy.USE_REMOTE, remoteOrder, reason);
    }
    
    public static ConflictResolution lastWriteWins(UUID conflictId, Order winningOrder, String reason) {
        return automatic(conflictId, ConflictResolutionStrategy.LAST_WRITE_WINS, winningOrder, reason);
    }
    
    public static ConflictResolution merge(UUID conflictId, Order mergedOrder, String reason) {
        return automatic(conflictId, ConflictResolutionStrategy.MERGE, mergedOrder, reason);
    }
    
    /**
     * Gets a human-readable description of the resolution.
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("Resolved using ").append(strategy.getDisplayName());
        
        if (automatic) {
            desc.append(" (automatic)");
        } else {
            desc.append(" (manual by ").append(resolvedBy).append(")");
        }
        
        if (reason != null && !reason.trim().isEmpty()) {
            desc.append(": ").append(reason);
        }
        
        return desc.toString();
    }
    
    @Override
    public String toString() {
        return "ConflictResolution{" +
                "conflictId=" + conflictId +
                ", strategy=" + strategy +
                ", reason='" + reason + '\'' +
                ", resolvedAt=" + resolvedAt +
                ", resolvedBy='" + resolvedBy + '\'' +
                ", automatic=" + automatic +
                '}';
    }
}

