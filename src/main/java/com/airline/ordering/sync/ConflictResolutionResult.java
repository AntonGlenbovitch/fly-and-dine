package com.airline.ordering.sync;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the result of a conflict resolution operation.
 */
public class ConflictResolutionResult {
    
    private final boolean success;
    private final LocalDateTime timestamp;
    private final List<UUID> resolvedConflictIds;
    private final List<UUID> unresolvedConflictIds;
    private final List<ConflictResolution> resolutions;
    private final List<SyncError> errors;
    private final String message;
    
    private ConflictResolutionResult(Builder builder) {
        this.success = builder.success;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.resolvedConflictIds = new ArrayList<>(builder.resolvedConflictIds);
        this.unresolvedConflictIds = new ArrayList<>(builder.unresolvedConflictIds);
        this.resolutions = new ArrayList<>(builder.resolutions);
        this.errors = new ArrayList<>(builder.errors);
        this.message = builder.message;
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public List<UUID> getResolvedConflictIds() {
        return new ArrayList<>(resolvedConflictIds);
    }
    
    public List<UUID> getUnresolvedConflictIds() {
        return new ArrayList<>(unresolvedConflictIds);
    }
    
    public List<ConflictResolution> getResolutions() {
        return new ArrayList<>(resolutions);
    }
    
    public List<SyncError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getResolvedCount() {
        return resolvedConflictIds.size();
    }
    
    public int getUnresolvedCount() {
        return unresolvedConflictIds.size();
    }
    
    public int getTotalCount() {
        return getResolvedCount() + getUnresolvedCount();
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasUnresolvedConflicts() {
        return !unresolvedConflictIds.isEmpty();
    }
    
    public boolean isCompletelyResolved() {
        return success && unresolvedConflictIds.isEmpty() && errors.isEmpty();
    }
    
    // Static factory methods
    public static ConflictResolutionResult success(String message) {
        return new Builder()
                .success(true)
                .message(message)
                .build();
    }
    
    public static ConflictResolutionResult failure(String message) {
        return new Builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static ConflictResolutionResult partial(String message) {
        return new Builder()
                .success(true)
                .message(message)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private boolean success;
        private LocalDateTime timestamp;
        private List<UUID> resolvedConflictIds = new ArrayList<>();
        private List<UUID> unresolvedConflictIds = new ArrayList<>();
        private List<ConflictResolution> resolutions = new ArrayList<>();
        private List<SyncError> errors = new ArrayList<>();
        private String message;
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder resolvedConflictIds(List<UUID> resolvedConflictIds) {
            this.resolvedConflictIds = new ArrayList<>(resolvedConflictIds);
            return this;
        }
        
        public Builder addResolvedConflictId(UUID conflictId) {
            this.resolvedConflictIds.add(conflictId);
            return this;
        }
        
        public Builder unresolvedConflictIds(List<UUID> unresolvedConflictIds) {
            this.unresolvedConflictIds = new ArrayList<>(unresolvedConflictIds);
            return this;
        }
        
        public Builder addUnresolvedConflictId(UUID conflictId) {
            this.unresolvedConflictIds.add(conflictId);
            return this;
        }
        
        public Builder resolutions(List<ConflictResolution> resolutions) {
            this.resolutions = new ArrayList<>(resolutions);
            return this;
        }
        
        public Builder addResolution(ConflictResolution resolution) {
            this.resolutions.add(resolution);
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
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public ConflictResolutionResult build() {
            return new ConflictResolutionResult(this);
        }
    }
    
    /**
     * Gets a summary of the resolution result.
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Conflict Resolution: ");
        
        if (isCompletelyResolved()) {
            summary.append("All ").append(getResolvedCount()).append(" conflicts resolved successfully");
        } else if (success) {
            summary.append(getResolvedCount()).append(" of ").append(getTotalCount())
                   .append(" conflicts resolved");
            if (hasUnresolvedConflicts()) {
                summary.append(" (").append(getUnresolvedCount()).append(" unresolved)");
            }
        } else {
            summary.append("Failed to resolve conflicts");
        }
        
        if (hasErrors()) {
            summary.append(" with ").append(errors.size()).append(" errors");
        }
        
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return "ConflictResolutionResult{" +
                "success=" + success +
                ", timestamp=" + timestamp +
                ", resolvedCount=" + getResolvedCount() +
                ", unresolvedCount=" + getUnresolvedCount() +
                ", errorCount=" + errors.size() +
                ", message='" + message + '\'' +
                '}';
    }
}

