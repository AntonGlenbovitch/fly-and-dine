package com.airline.ordering.security.auth;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Result of a device authentication attempt.
 */
public class AuthenticationResult {
    
    private final boolean successful;
    private final String deviceId;
    private final String accessToken;
    private final String refreshToken;
    private final LocalDateTime tokenExpiresAt;
    private final LocalDateTime refreshExpiresAt;
    private final String message;
    private final AuthFailureReason failureReason;
    private final Map<String, Object> metadata;
    private final LocalDateTime timestamp;
    
    private AuthenticationResult(Builder builder) {
        this.successful = builder.successful;
        this.deviceId = builder.deviceId;
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.tokenExpiresAt = builder.tokenExpiresAt;
        this.refreshExpiresAt = builder.refreshExpiresAt;
        this.message = builder.message;
        this.failureReason = builder.failureReason;
        this.metadata = new HashMap<>(builder.metadata);
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public boolean isSuccessful() { return successful; }
    public String getDeviceId() { return deviceId; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public LocalDateTime getTokenExpiresAt() { return tokenExpiresAt; }
    public LocalDateTime getRefreshExpiresAt() { return refreshExpiresAt; }
    public String getMessage() { return message; }
    public AuthFailureReason getFailureReason() { return failureReason; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    // Utility methods
    public boolean hasValidToken() {
        return successful && accessToken != null && 
               (tokenExpiresAt == null || LocalDateTime.now().isBefore(tokenExpiresAt));
    }
    
    public boolean canRefresh() {
        return refreshToken != null && 
               (refreshExpiresAt == null || LocalDateTime.now().isBefore(refreshExpiresAt));
    }
    
    public long getTokenValidityMinutes() {
        if (tokenExpiresAt == null) return Long.MAX_VALUE;
        return java.time.Duration.between(LocalDateTime.now(), tokenExpiresAt).toMinutes();
    }
    
    // Static factory methods
    public static AuthenticationResult success(String deviceId, String accessToken, String refreshToken) {
        return builder()
                .successful(true)
                .deviceId(deviceId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Authentication successful")
                .build();
    }
    
    public static AuthenticationResult failure(String deviceId, AuthFailureReason reason, String message) {
        return builder()
                .successful(false)
                .deviceId(deviceId)
                .failureReason(reason)
                .message(message)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private boolean successful;
        private String deviceId;
        private String accessToken;
        private String refreshToken;
        private LocalDateTime tokenExpiresAt;
        private LocalDateTime refreshExpiresAt;
        private String message;
        private AuthFailureReason failureReason;
        private Map<String, Object> metadata = new HashMap<>();
        
        public Builder successful(boolean successful) { this.successful = successful; return this; }
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public Builder tokenExpiresAt(LocalDateTime tokenExpiresAt) { this.tokenExpiresAt = tokenExpiresAt; return this; }
        public Builder refreshExpiresAt(LocalDateTime refreshExpiresAt) { this.refreshExpiresAt = refreshExpiresAt; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder failureReason(AuthFailureReason failureReason) { this.failureReason = failureReason; return this; }
        public Builder metadata(String key, Object value) { this.metadata.put(key, value); return this; }
        public Builder metadata(Map<String, Object> metadata) { this.metadata = new HashMap<>(metadata); return this; }
        
        public AuthenticationResult build() {
            return new AuthenticationResult(this);
        }
    }
    
    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "successful=" + successful +
                ", deviceId='" + deviceId + '\'' +
                ", hasToken=" + (accessToken != null) +
                ", message='" + message + '\'' +
                ", failureReason=" + failureReason +
                ", timestamp=" + timestamp +
                '}';
    }
    
    /**
     * Reasons for authentication failure.
     */
    public enum AuthFailureReason {
        INVALID_CREDENTIALS("Invalid credentials provided"),
        DEVICE_NOT_REGISTERED("Device is not registered"),
        DEVICE_REVOKED("Device has been revoked"),
        DEVICE_NON_COMPLIANT("Device does not meet security requirements"),
        TOKEN_EXPIRED("Authentication token has expired"),
        RATE_LIMITED("Too many authentication attempts"),
        NETWORK_ERROR("Network error during authentication"),
        SERVICE_UNAVAILABLE("Authentication service is unavailable"),
        UNKNOWN_ERROR("Unknown authentication error");
        
        private final String description;
        
        AuthFailureReason(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
        
        @Override
        public String toString() { return description; }
    }
}

