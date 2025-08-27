package com.airline.ordering.sync;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the result of a synchronization operation.
 * Contains details about success/failure, processed items, and any errors encountered.
 */
public class SyncResult {
    
    private final boolean success;
    private final LocalDateTime timestamp;
    private final SyncOperation operation;
    private final List<UUID> processedOrderIds;
    private final List<UUID> failedOrderIds;
    private final List<SyncError> errors;
    private final List<ConflictInfo> conflicts;
    private final SyncStatistics statistics;
    private final String message;
    
    private SyncResult(Builder builder) {
        this.success = builder.success;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.operation = builder.operation;
        this.processedOrderIds = new ArrayList<>(builder.processedOrderIds);
        this.failedOrderIds = new ArrayList<>(builder.failedOrderIds);
        this.errors = new ArrayList<>(builder.errors);
        this.conflicts = new ArrayList<>(builder.conflicts);
        this.statistics = builder.statistics;
        this.message = builder.message;
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public SyncOperation getOperation() {
        return operation;
    }
    
    public List<UUID> getProcessedOrderIds() {
        return new ArrayList<>(processedOrderIds);
    }
    
    public List<UUID> getFailedOrderIds() {
        return new ArrayList<>(failedOrderIds);
    }
    
    public List<SyncError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public List<ConflictInfo> getConflicts() {
        return new ArrayList<>(conflicts);
    }
    
    public SyncStatistics getStatistics() {
        return statistics;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }
    
    public int getProcessedCount() {
        return processedOrderIds.size();
    }
    
    public int getFailedCount() {
        return failedOrderIds.size();
    }
    
    public int getConflictCount() {
        return conflicts.size();
    }
    
    // Static factory methods
    public static SyncResult success(SyncOperation operation, String message) {
        return new Builder()
                .success(true)
                .operation(operation)
                .message(message)
                .build();
    }
    
    public static SyncResult failure(SyncOperation operation, String message) {
        return new Builder()
                .success(false)
                .operation(operation)
                .message(message)
                .build();
    }
    
    public static SyncResult failure(SyncOperation operation, String message, List<SyncError> errors) {
        return new Builder()
                .success(false)
                .operation(operation)
                .message(message)
                .errors(errors)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private boolean success;
        private LocalDateTime timestamp;
        private SyncOperation operation;
        private List<UUID> processedOrderIds = new ArrayList<>();
        private List<UUID> failedOrderIds = new ArrayList<>();
        private List<SyncError> errors = new ArrayList<>();
        private List<ConflictInfo> conflicts = new ArrayList<>();
        private SyncStatistics statistics;
        private String message;
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder operation(SyncOperation operation) {
            this.operation = operation;
            return this;
        }
        
        public Builder processedOrderIds(List<UUID> processedOrderIds) {
            this.processedOrderIds = new ArrayList<>(processedOrderIds);
            return this;
        }
        
        public Builder addProcessedOrderId(UUID orderId) {
            this.processedOrderIds.add(orderId);
            return this;
        }
        
        public Builder failedOrderIds(List<UUID> failedOrderIds) {
            this.failedOrderIds = new ArrayList<>(failedOrderIds);
            return this;
        }
        
        public Builder addFailedOrderId(UUID orderId) {
            this.failedOrderIds.add(orderId);
            return this;
        }
        
        public Builder errors(List<SyncError> errors) {
            this.errors = new ArrayList<>(errors);
            return this;
        }
        
        public Builder addError(SyncError error) {
            this.errors.add(error);
            return this;
        }
        
        public Builder conflicts(List<ConflictInfo> conflicts) {
            this.conflicts = new ArrayList<>(conflicts);
            return this;
        }
        
        public Builder addConflict(ConflictInfo conflict) {
            this.conflicts.add(conflict);
            return this;
        }
        
        public Builder statistics(SyncStatistics statistics) {
            this.statistics = statistics;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public SyncResult build() {
            return new SyncResult(this);
        }
    }
    
    @Override
    public String toString() {
        return "SyncResult{" +
                "success=" + success +
                ", timestamp=" + timestamp +
                ", operation=" + operation +
                ", processedCount=" + processedOrderIds.size() +
                ", failedCount=" + failedOrderIds.size() +
                ", errorCount=" + errors.size() +
                ", conflictCount=" + conflicts.size() +
                ", message='" + message + '\'' +
                '}';
    }
}

