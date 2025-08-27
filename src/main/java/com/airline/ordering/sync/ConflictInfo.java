package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a data conflict between local and remote versions of an order.
 */
public class ConflictInfo {
    
    private final UUID orderId;
    private final Order localVersion;
    private final Order remoteVersion;
    private final ConflictType conflictType;
    private final String description;
    private final LocalDateTime detectedAt;
    private final ConflictSeverity severity;
    private ConflictResolution resolution;
    
    public ConflictInfo(UUID orderId, Order localVersion, Order remoteVersion, 
                       ConflictType conflictType, String description) {
        this.orderId = orderId;
        this.localVersion = localVersion;
        this.remoteVersion = remoteVersion;
        this.conflictType = conflictType;
        this.description = description;
        this.detectedAt = LocalDateTime.now();
        this.severity = determineSeverity(conflictType, localVersion, remoteVersion);
    }
    
    // Getters
    public UUID getOrderId() {
        return orderId;
    }
    
    public Order getLocalVersion() {
        return localVersion;
    }
    
    public Order getRemoteVersion() {
        return remoteVersion;
    }
    
    public ConflictType getConflictType() {
        return conflictType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }
    
    public ConflictSeverity getSeverity() {
        return severity;
    }
    
    public ConflictResolution getResolution() {
        return resolution;
    }
    
    public void setResolution(ConflictResolution resolution) {
        this.resolution = resolution;
    }
    
    public boolean isResolved() {
        return resolution != null;
    }
    
    /**
     * Determines the severity of the conflict based on the type and order differences.
     */
    private ConflictSeverity determineSeverity(ConflictType type, Order local, Order remote) {
        switch (type) {
            case STATUS_CONFLICT:
                // Status conflicts are usually high severity
                return ConflictSeverity.HIGH;
                
            case CONTENT_CONFLICT:
                // Check if total amounts differ significantly
                if (local != null && remote != null) {
                    double localAmount = local.getTotalAmount().doubleValue();
                    double remoteAmount = remote.getTotalAmount().doubleValue();
                    double difference = Math.abs(localAmount - remoteAmount);
                    
                    if (difference > 50.0) {
                        return ConflictSeverity.HIGH;
                    } else if (difference > 10.0) {
                        return ConflictSeverity.MEDIUM;
                    }
                }
                return ConflictSeverity.LOW;
                
            case TIMESTAMP_CONFLICT:
                return ConflictSeverity.MEDIUM;
                
            case DELETION_CONFLICT:
                return ConflictSeverity.HIGH;
                
            default:
                return ConflictSeverity.MEDIUM;
        }
    }
    
    /**
     * Gets a human-readable summary of the conflict.
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(conflictType.getDisplayName()).append(" for order ").append(orderId);
        
        if (localVersion != null && remoteVersion != null) {
            summary.append(" (Local: ").append(localVersion.getStatus())
                   .append(", Remote: ").append(remoteVersion.getStatus()).append(")");
        }
        
        return summary.toString();
    }
    
    /**
     * Checks if this conflict can be automatically resolved.
     */
    public boolean isAutoResolvable() {
        return conflictType.isAutoResolvable() && severity != ConflictSeverity.HIGH;
    }
    
    @Override
    public String toString() {
        return "ConflictInfo{" +
                "orderId=" + orderId +
                ", conflictType=" + conflictType +
                ", severity=" + severity +
                ", description='" + description + '\'' +
                ", detectedAt=" + detectedAt +
                ", resolved=" + isResolved() +
                '}';
    }
}

