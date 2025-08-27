package com.airline.ordering.sync;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Contains statistics about a synchronization operation.
 */
public class SyncStatistics {
    
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Duration duration;
    private final int totalRecords;
    private final int successfulRecords;
    private final int failedRecords;
    private final int conflictedRecords;
    private final int skippedRecords;
    private final long bytesTransferred;
    private final int networkRequests;
    private final int retryAttempts;
    
    private SyncStatistics(Builder builder) {
        this.startTime = builder.startTime;
        this.endTime = builder.endTime != null ? builder.endTime : LocalDateTime.now();
        this.duration = Duration.between(startTime, endTime);
        this.totalRecords = builder.totalRecords;
        this.successfulRecords = builder.successfulRecords;
        this.failedRecords = builder.failedRecords;
        this.conflictedRecords = builder.conflictedRecords;
        this.skippedRecords = builder.skippedRecords;
        this.bytesTransferred = builder.bytesTransferred;
        this.networkRequests = builder.networkRequests;
        this.retryAttempts = builder.retryAttempts;
    }
    
    // Getters
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    public int getSuccessfulRecords() {
        return successfulRecords;
    }
    
    public int getFailedRecords() {
        return failedRecords;
    }
    
    public int getConflictedRecords() {
        return conflictedRecords;
    }
    
    public int getSkippedRecords() {
        return skippedRecords;
    }
    
    public long getBytesTransferred() {
        return bytesTransferred;
    }
    
    public int getNetworkRequests() {
        return networkRequests;
    }
    
    public int getRetryAttempts() {
        return retryAttempts;
    }
    
    // Calculated properties
    public double getSuccessRate() {
        return totalRecords > 0 ? (double) successfulRecords / totalRecords * 100 : 0;
    }
    
    public double getFailureRate() {
        return totalRecords > 0 ? (double) failedRecords / totalRecords * 100 : 0;
    }
    
    public double getConflictRate() {
        return totalRecords > 0 ? (double) conflictedRecords / totalRecords * 100 : 0;
    }
    
    public double getRecordsPerSecond() {
        long seconds = duration.getSeconds();
        return seconds > 0 ? (double) totalRecords / seconds : 0;
    }
    
    public double getBytesPerSecond() {
        long seconds = duration.getSeconds();
        return seconds > 0 ? (double) bytesTransferred / seconds : 0;
    }
    
    public double getAverageRequestTime() {
        return networkRequests > 0 ? (double) duration.toMillis() / networkRequests : 0;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private LocalDateTime startTime = LocalDateTime.now();
        private LocalDateTime endTime;
        private int totalRecords;
        private int successfulRecords;
        private int failedRecords;
        private int conflictedRecords;
        private int skippedRecords;
        private long bytesTransferred;
        private int networkRequests;
        private int retryAttempts;
        
        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public Builder totalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
            return this;
        }
        
        public Builder successfulRecords(int successfulRecords) {
            this.successfulRecords = successfulRecords;
            return this;
        }
        
        public Builder failedRecords(int failedRecords) {
            this.failedRecords = failedRecords;
            return this;
        }
        
        public Builder conflictedRecords(int conflictedRecords) {
            this.conflictedRecords = conflictedRecords;
            return this;
        }
        
        public Builder skippedRecords(int skippedRecords) {
            this.skippedRecords = skippedRecords;
            return this;
        }
        
        public Builder bytesTransferred(long bytesTransferred) {
            this.bytesTransferred = bytesTransferred;
            return this;
        }
        
        public Builder networkRequests(int networkRequests) {
            this.networkRequests = networkRequests;
            return this;
        }
        
        public Builder retryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
            return this;
        }
        
        public SyncStatistics build() {
            return new SyncStatistics(this);
        }
    }
    
    /**
     * Gets a human-readable summary of the statistics.
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Sync Statistics: ");
        summary.append(successfulRecords).append("/").append(totalRecords).append(" successful");
        summary.append(" (").append(String.format("%.1f", getSuccessRate())).append("%)");
        summary.append(" in ").append(duration.getSeconds()).append("s");
        
        if (failedRecords > 0) {
            summary.append(", ").append(failedRecords).append(" failed");
        }
        
        if (conflictedRecords > 0) {
            summary.append(", ").append(conflictedRecords).append(" conflicts");
        }
        
        if (retryAttempts > 0) {
            summary.append(", ").append(retryAttempts).append(" retries");
        }
        
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return "SyncStatistics{" +
                "duration=" + duration +
                ", totalRecords=" + totalRecords +
                ", successfulRecords=" + successfulRecords +
                ", failedRecords=" + failedRecords +
                ", conflictedRecords=" + conflictedRecords +
                ", skippedRecords=" + skippedRecords +
                ", bytesTransferred=" + bytesTransferred +
                ", networkRequests=" + networkRequests +
                ", retryAttempts=" + retryAttempts +
                ", successRate=" + String.format("%.1f", getSuccessRate()) + "%" +
                '}';
    }
}

