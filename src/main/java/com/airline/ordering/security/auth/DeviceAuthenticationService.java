package com.airline.ordering.security.auth;

import com.airline.ordering.security.SecurityStubs.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for managing device authentication and provisioning.
 * Handles device registration, authentication, and lifecycle management.
 */
public interface DeviceAuthenticationService {
    
    /**
     * Provisions a new device for use with the application.
     * 
     * @param deviceInfo information about the device to provision
     * @return DeviceProvisioningResult containing the provisioning outcome
     */
    DeviceProvisioningResult provisionDevice(DeviceInfo deviceInfo);
    
    /**
     * Authenticates a device using its credentials.
     * 
     * @param deviceId the unique device identifier
     * @param deviceCredentials the device authentication credentials
     * @return AuthenticationResult containing the authentication outcome
     */
    AuthenticationResult authenticateDevice(String deviceId, DeviceCredentials deviceCredentials);
    
    /**
     * Refreshes the authentication token for a device.
     * 
     * @param deviceId the unique device identifier
     * @param refreshToken the refresh token
     * @return AuthenticationResult containing the new tokens
     */
    AuthenticationResult refreshDeviceToken(String deviceId, String refreshToken);
    
    /**
     * Revokes authentication for a device.
     * 
     * @param deviceId the unique device identifier
     * @param reason the reason for revocation
     * @return true if revocation was successful, false otherwise
     */
    boolean revokeDevice(String deviceId, String reason);
    
    /**
     * Gets information about a registered device.
     * 
     * @param deviceId the unique device identifier
     * @return DeviceInfo if the device exists, null otherwise
     */
    DeviceInfo getDeviceInfo(String deviceId);
    
    /**
     * Lists all devices registered for a specific user or crew member.
     * 
     * @param userId the user identifier
     * @return list of devices associated with the user
     */
    List<DeviceInfo> getDevicesForUser(String userId);
    
    /**
     * Updates device information (e.g., after OS update, app update).
     * 
     * @param deviceId the unique device identifier
     * @param updatedInfo the updated device information
     * @return true if update was successful, false otherwise
     */
    boolean updateDeviceInfo(String deviceId, DeviceInfo updatedInfo);
    
    /**
     * Checks if a device is currently authenticated and authorized.
     * 
     * @param deviceId the unique device identifier
     * @param accessToken the access token to verify
     * @return true if the device is authenticated, false otherwise
     */
    boolean isDeviceAuthenticated(String deviceId, String accessToken);
    
    /**
     * Gets the current authentication status of a device.
     * 
     * @param deviceId the unique device identifier
     * @return the current authentication status
     */
    DeviceAuthStatus getDeviceAuthStatus(String deviceId);
    
    /**
     * Validates device compliance with security policies.
     * 
     * @param deviceInfo the device information to validate
     * @return DeviceComplianceResult containing compliance status
     */
    DeviceComplianceResult validateDeviceCompliance(DeviceInfo deviceInfo);
    
    /**
     * Generates a device fingerprint for additional security.
     * 
     * @param deviceInfo the device information
     * @return a unique fingerprint for the device
     */
    String generateDeviceFingerprint(DeviceInfo deviceInfo);
    
    /**
     * Verifies a device fingerprint against stored information.
     * 
     * @param deviceId the unique device identifier
     * @param fingerprint the fingerprint to verify
     * @return true if the fingerprint matches, false otherwise
     */
    boolean verifyDeviceFingerprint(String deviceId, String fingerprint);
    
    /**
     * Logs device authentication events for audit purposes.
     * 
     * @param deviceId the unique device identifier
     * @param event the authentication event
     * @param details additional event details
     */
    void logAuthenticationEvent(String deviceId, AuthEvent event, Map<String, Object> details);
    
    /**
     * Gets authentication history for a device.
     * 
     * @param deviceId the unique device identifier
     * @param since the start date for the history
     * @return list of authentication events
     */
    List<AuthEvent> getAuthenticationHistory(String deviceId, LocalDateTime since);
    
    /**
     * Performs device health check to ensure security compliance.
     * 
     * @param deviceId the unique device identifier
     * @return DeviceHealthStatus containing health information
     */
    DeviceHealthStatus checkDeviceHealth(String deviceId);
    
    /**
     * Initiates device wipe for lost or compromised devices.
     * 
     * @param deviceId the unique device identifier
     * @param reason the reason for the wipe
     * @return true if wipe command was sent successfully, false otherwise
     */
    boolean initiateDeviceWipe(String deviceId, String reason);
    
    /**
     * Sets up device for offline operation with cached credentials.
     * 
     * @param deviceId the unique device identifier
     * @param offlineDuration the duration for offline operation
     * @return OfflineSetupResult containing offline configuration
     */
    OfflineSetupResult setupOfflineOperation(String deviceId, java.time.Duration offlineDuration);
    
    /**
     * Validates offline credentials when network is unavailable.
     * 
     * @param deviceId the unique device identifier
     * @param offlineCredentials the cached offline credentials
     * @return true if offline credentials are valid, false otherwise
     */
    boolean validateOfflineCredentials(String deviceId, String offlineCredentials);
}

