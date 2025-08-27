package com.airline.ordering.security.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * Represents an audit event in the system.
 * Contains all information needed for compliance and security monitoring.
 */
public class AuditEvent {
    
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String deviceId;
    private final String sessionId;
    private final AuditEventType eventType;
    private final String action;
    private final String resource;
    private final String resourceId;
    private final AuditOutcome outcome;
    private final AuditSeverity severity;
    private final String message;
    private final String ipAddress;
    private final String userAgent;
    private final String location;
    private final Map<String, Object> details;
    private final Map<String, Object> beforeState;
    private final Map<String, Object> afterState;
    private final String correlationId;
    private final long duration;
    private final String checksum;
    
    private AuditEvent(Builder builder) {
        this.eventId = builder.eventId != null ? builder.eventId : UUID.randomUUID().toString();
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.userId = builder.userId;
        this.deviceId = builder.deviceId;
        this.sessionId = builder.sessionId;
        this.eventType = builder.eventType;
        this.action = builder.action;
        this.resource = builder.resource;
        this.resourceId = builder.resourceId;
        this.outcome = builder.outcome;
        this.severity = builder.severity;
        this.message = builder.message;
        this.ipAddress = builder.ipAddress;
        this.userAgent = builder.userAgent;
        this.location = builder.location;
        this.details = new HashMap<>(builder.details);
        this.beforeState = new HashMap<>(builder.beforeState);
        this.afterState = new HashMap<>(builder.afterState);
        this.correlationId = builder.correlationId;
        this.duration = builder.duration;
        this.checksum = calculateChecksum();
    }
    
    // Getters
    public String getEventId() { return eventId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getDeviceId() { return deviceId; }
    public String getSessionId() { return sessionId; }
    public AuditEventType getEventType() { return eventType; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public String getResourceId() { return resourceId; }
    public AuditOutcome getOutcome() { return outcome; }
    public AuditSeverity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getLocation() { return location; }
    public Map<String, Object> getDetails() { return new HashMap<>(details); }
    public Map<String, Object> getBeforeState() { return new HashMap<>(beforeState); }
    public Map<String, Object> getAfterState() { return new HashMap<>(afterState); }
    public String getCorrelationId() { return correlationId; }
    public long getDuration() { return duration; }
    public String getChecksum() { return checksum; }
    
    // Utility methods
    public boolean isSecurityEvent() {
        return eventType == AuditEventType.SECURITY || 
               eventType == AuditEventType.AUTHENTICATION ||
               eventType == AuditEventType.AUTHORIZATION;
    }
    
    public boolean isDataEvent() {
        return eventType == AuditEventType.DATA_ACCESS ||
               eventType == AuditEventType.DATA_MODIFICATION;
    }
    
    public boolean isFailure() {
        return outcome == AuditOutcome.FAILURE || outcome == AuditOutcome.ERROR;
    }
    
    public boolean isHighSeverity() {
        return severity == AuditSeverity.HIGH || severity == AuditSeverity.CRITICAL;
    }
    
    public Object getDetail(String key) {
        return details.get(key);
    }
    
    public boolean hasStateChange() {
        return !beforeState.isEmpty() || !afterState.isEmpty();
    }
    
    private String calculateChecksum() {
        // Simple checksum calculation for tamper detection
        StringBuilder sb = new StringBuilder();
        sb.append(eventId).append(timestamp).append(userId).append(action)
          .append(resource).append(outcome).append(message);
        
        return Integer.toHexString(sb.toString().hashCode());
    }
    
    public boolean verifyIntegrity() {
        return checksum.equals(calculateChecksum());
    }
    
    // Static factory methods
    public static AuditEvent securityEvent(String userId, String action, AuditOutcome outcome, String message) {
        return builder()
                .eventType(AuditEventType.SECURITY)
                .userId(userId)
                .action(action)
                .outcome(outcome)
                .severity(outcome == AuditOutcome.FAILURE ? AuditSeverity.HIGH : AuditSeverity.MEDIUM)
                .message(message)
                .build();
    }
    
    public static AuditEvent dataAccessEvent(String userId, String resource, String resourceId, String action) {
        return builder()
                .eventType(AuditEventType.DATA_ACCESS)
                .userId(userId)
                .resource(resource)
                .resourceId(resourceId)
                .action(action)
                .outcome(AuditOutcome.SUCCESS)
                .severity(AuditSeverity.LOW)
                .message("Data access: " + action + " on " + resource)
                .build();
    }
    
    public static AuditEvent authenticationEvent(String deviceId, String userId, AuditOutcome outcome, String ipAddress) {
        return builder()
                .eventType(AuditEventType.AUTHENTICATION)
                .deviceId(deviceId)
                .userId(userId)
                .action("authenticate")
                .outcome(outcome)
                .severity(outcome == AuditOutcome.FAILURE ? AuditSeverity.MEDIUM : AuditSeverity.LOW)
                .ipAddress(ipAddress)
                .message("Authentication attempt")
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private String eventId;
        private LocalDateTime timestamp;
        private String userId;
        private String deviceId;
        private String sessionId;
        private AuditEventType eventType;
        private String action;
        private String resource;
        private String resourceId;
        private AuditOutcome outcome;
        private AuditSeverity severity;
        private String message;
        private String ipAddress;
        private String userAgent;
        private String location;
        private Map<String, Object> details = new HashMap<>();
        private Map<String, Object> beforeState = new HashMap<>();
        private Map<String, Object> afterState = new HashMap<>();
        private String correlationId;
        private long duration;
        
        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public Builder eventType(AuditEventType eventType) { this.eventType = eventType; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder outcome(AuditOutcome outcome) { this.outcome = outcome; return this; }
        public Builder severity(AuditSeverity severity) { this.severity = severity; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder detail(String key, Object value) { this.details.put(key, value); return this; }
        public Builder details(Map<String, Object> details) { this.details = new HashMap<>(details); return this; }
        public Builder beforeState(Map<String, Object> beforeState) { this.beforeState = new HashMap<>(beforeState); return this; }
        public Builder afterState(Map<String, Object> afterState) { this.afterState = new HashMap<>(afterState); return this; }
        public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
        public Builder duration(long duration) { this.duration = duration; return this; }
        
        public AuditEvent build() {
            return new AuditEvent(this);
        }
    }
    
    @Override
    public String toString() {
        return "AuditEvent{" +
                "eventId='" + eventId + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", eventType=" + eventType +
                ", action='" + action + '\'' +
                ", resource='" + resource + '\'' +
                ", outcome=" + outcome +
                ", severity=" + severity +
                ", message='" + message + '\'' +
                '}';
    }
}

