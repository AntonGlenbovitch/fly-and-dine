package com.airline.ordering.sync;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the health status of the Core Reservation System (CRS).
 */
public class CRSHealthStatus {
    
    private final boolean healthy;
    private final LocalDateTime timestamp;
    private final long responseTimeMs;
    private final String version;
    private final Map<String, Object> details;
    private final CRSHealthLevel level;
    
    public CRSHealthStatus(boolean healthy, long responseTimeMs, String version, 
                          Map<String, Object> details, CRSHealthLevel level) {
        this.healthy = healthy;
        this.timestamp = LocalDateTime.now();
        this.responseTimeMs = responseTimeMs;
        this.version = version;
        this.details = new HashMap<>(details != null ? details : new HashMap<>());
        this.level = level;
    }
    
    // Getters
    public boolean isHealthy() {
        return healthy;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public long getResponseTimeMs() {
        return responseTimeMs;
    }
    
    public String getVersion() {
        return version;
    }
    
    public Map<String, Object> getDetails() {
        return new HashMap<>(details);
    }
    
    public CRSHealthLevel getLevel() {
        return level;
    }
    
    // Convenience methods
    public Object getDetail(String key) {
        return details.get(key);
    }
    
    public String getDetailAsString(String key) {
        Object value = details.get(key);
        return value != null ? value.toString() : null;
    }
    
    public boolean isResponseTimeAcceptable() {
        return responseTimeMs < 5000; // 5 seconds threshold
    }
    
    public boolean isPerformanceGood() {
        return responseTimeMs < 1000; // 1 second threshold
    }
    
    // Static factory methods
    public static CRSHealthStatus healthy(long responseTimeMs, String version) {
        return new CRSHealthStatus(true, responseTimeMs, version, null, CRSHealthLevel.GOOD);
    }
    
    public static CRSHealthStatus degraded(long responseTimeMs, String version, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("reason", reason);
        return new CRSHealthStatus(true, responseTimeMs, version, details, CRSHealthLevel.DEGRADED);
    }
    
    public static CRSHealthStatus unhealthy(String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("reason", reason);
        return new CRSHealthStatus(false, -1, null, details, CRSHealthLevel.CRITICAL);
    }
    
    @Override
    public String toString() {
        return "CRSHealthStatus{" +
                "healthy=" + healthy +
                ", timestamp=" + timestamp +
                ", responseTimeMs=" + responseTimeMs +
                ", version='" + version + '\'' +
                ", level=" + level +
                '}';
    }
    
    /**
     * Enumeration of CRS health levels.
     */
    public enum CRSHealthLevel {
        GOOD("Good", "CRS is operating normally"),
        DEGRADED("Degraded", "CRS is operating but with reduced performance"),
        POOR("Poor", "CRS is operating but with significant issues"),
        CRITICAL("Critical", "CRS is not functioning properly");
        
        private final String displayName;
        private final String description;
        
        CRSHealthLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}

