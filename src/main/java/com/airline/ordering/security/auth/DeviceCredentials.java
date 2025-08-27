package com.airline.ordering.security.auth;

import java.time.LocalDateTime;

/**
 * Credentials used for device authentication.
 */
public class DeviceCredentials {
    
    private final String deviceId;
    private final String credentialType;
    private final String credential;
    private final String salt;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    
    public DeviceCredentials(String deviceId, String credentialType, String credential, String salt) {
        this(deviceId, credentialType, credential, salt, LocalDateTime.now(), null);
    }
    
    public DeviceCredentials(String deviceId, String credentialType, String credential, String salt, 
                           LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.deviceId = deviceId;
        this.credentialType = credentialType;
        this.credential = credential;
        this.salt = salt;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
    
    // Getters
    public String getDeviceId() { return deviceId; }
    public String getCredentialType() { return credentialType; }
    public String getCredential() { return credential; }
    public String getSalt() { return salt; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    
    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !isExpired() && credential != null && !credential.isEmpty();
    }
    
    @Override
    public String toString() {
        return "DeviceCredentials{" +
                "deviceId='" + deviceId + '\'' +
                ", credentialType='" + credentialType + '\'' +
                ", isValid=" + isValid() +
                '}';
    }
}

