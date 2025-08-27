package com.airline.ordering.security.auth;

import java.time.LocalDateTime;

/**
 * Result of device provisioning operation.
 */
public class DeviceProvisioningResult {
    
    private final boolean successful;
    private final String deviceId;
    private final DeviceCredentials credentials;
    private final String message;
    private final String errorCode;
    private final LocalDateTime timestamp;
    
    public DeviceProvisioningResult(boolean successful, String deviceId, DeviceCredentials credentials, 
                                  String message, String errorCode) {
        this.successful = successful;
        this.deviceId = deviceId;
        this.credentials = credentials;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public boolean isSuccessful() { return successful; }
    public String getDeviceId() { return deviceId; }
    public DeviceCredentials getCredentials() { return credentials; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    // Static factory methods
    public static DeviceProvisioningResult success(String deviceId, DeviceCredentials credentials) {
        return new DeviceProvisioningResult(true, deviceId, credentials, "Device provisioned successfully", null);
    }
    
    public static DeviceProvisioningResult failure(String deviceId, String message, String errorCode) {
        return new DeviceProvisioningResult(false, deviceId, null, message, errorCode);
    }
}

