package com.airline.ordering.security.auth;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Information about a device registered with the system.
 */
public class DeviceInfo {
    
    private final String deviceId;
    private final String deviceName;
    private final DeviceType deviceType;
    private final String operatingSystem;
    private final String osVersion;
    private final String appVersion;
    private final String manufacturer;
    private final String model;
    private final String serialNumber;
    private final String macAddress;
    private final String imei;
    private final boolean isJailbroken;
    private final boolean hasScreenLock;
    private final boolean hasEncryption;
    private final LocalDateTime registeredAt;
    private final LocalDateTime lastSeenAt;
    private final String userId;
    private final String userRole;
    private final Map<String, Object> additionalProperties;
    
    private DeviceInfo(Builder builder) {
        this.deviceId = builder.deviceId;
        this.deviceName = builder.deviceName;
        this.deviceType = builder.deviceType;
        this.operatingSystem = builder.operatingSystem;
        this.osVersion = builder.osVersion;
        this.appVersion = builder.appVersion;
        this.manufacturer = builder.manufacturer;
        this.model = builder.model;
        this.serialNumber = builder.serialNumber;
        this.macAddress = builder.macAddress;
        this.imei = builder.imei;
        this.isJailbroken = builder.isJailbroken;
        this.hasScreenLock = builder.hasScreenLock;
        this.hasEncryption = builder.hasEncryption;
        this.registeredAt = builder.registeredAt != null ? builder.registeredAt : LocalDateTime.now();
        this.lastSeenAt = builder.lastSeenAt;
        this.userId = builder.userId;
        this.userRole = builder.userRole;
        this.additionalProperties = new HashMap<>(builder.additionalProperties);
    }
    
    // Getters
    public String getDeviceId() { return deviceId; }
    public String getDeviceName() { return deviceName; }
    public DeviceType getDeviceType() { return deviceType; }
    public String getOperatingSystem() { return operatingSystem; }
    public String getOsVersion() { return osVersion; }
    public String getAppVersion() { return appVersion; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public String getSerialNumber() { return serialNumber; }
    public String getMacAddress() { return macAddress; }
    public String getImei() { return imei; }
    public boolean isJailbroken() { return isJailbroken; }
    public boolean hasScreenLock() { return hasScreenLock; }
    public boolean hasEncryption() { return hasEncryption; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public String getUserId() { return userId; }
    public String getUserRole() { return userRole; }
    public Map<String, Object> getAdditionalProperties() { return new HashMap<>(additionalProperties); }
    
    // Utility methods
    public boolean isMobile() {
        return deviceType == DeviceType.SMARTPHONE || deviceType == DeviceType.TABLET;
    }
    
    public boolean isSecure() {
        return !isJailbroken && hasScreenLock && hasEncryption;
    }
    
    public boolean isCompliant() {
        // Basic compliance check - can be extended based on requirements
        return isSecure() && appVersion != null && !appVersion.isEmpty();
    }
    
    public String getDisplayName() {
        if (deviceName != null && !deviceName.trim().isEmpty()) {
            return deviceName;
        }
        return manufacturer + " " + model;
    }
    
    public Object getAdditionalProperty(String key) {
        return additionalProperties.get(key);
    }
    
    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }
    
    public static DeviceInfo createMobile(String deviceId, String deviceName, String userId) {
        return builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(DeviceType.SMARTPHONE)
                .userId(userId)
                .build();
    }
    
    public static DeviceInfo createTablet(String deviceId, String deviceName, String userId) {
        return builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(DeviceType.TABLET)
                .userId(userId)
                .build();
    }
    
    public static DeviceInfo createLaptop(String deviceId, String deviceName, String userId) {
        return builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(DeviceType.LAPTOP)
                .userId(userId)
                .build();
    }
    
    // Builder pattern
    public static class Builder {
        private String deviceId;
        private String deviceName;
        private DeviceType deviceType;
        private String operatingSystem;
        private String osVersion;
        private String appVersion;
        private String manufacturer;
        private String model;
        private String serialNumber;
        private String macAddress;
        private String imei;
        private boolean isJailbroken;
        private boolean hasScreenLock;
        private boolean hasEncryption;
        private LocalDateTime registeredAt;
        private LocalDateTime lastSeenAt;
        private String userId;
        private String userRole;
        private Map<String, Object> additionalProperties = new HashMap<>();
        
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder deviceName(String deviceName) { this.deviceName = deviceName; return this; }
        public Builder deviceType(DeviceType deviceType) { this.deviceType = deviceType; return this; }
        public Builder operatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; return this; }
        public Builder osVersion(String osVersion) { this.osVersion = osVersion; return this; }
        public Builder appVersion(String appVersion) { this.appVersion = appVersion; return this; }
        public Builder manufacturer(String manufacturer) { this.manufacturer = manufacturer; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder serialNumber(String serialNumber) { this.serialNumber = serialNumber; return this; }
        public Builder macAddress(String macAddress) { this.macAddress = macAddress; return this; }
        public Builder imei(String imei) { this.imei = imei; return this; }
        public Builder isJailbroken(boolean isJailbroken) { this.isJailbroken = isJailbroken; return this; }
        public Builder hasScreenLock(boolean hasScreenLock) { this.hasScreenLock = hasScreenLock; return this; }
        public Builder hasEncryption(boolean hasEncryption) { this.hasEncryption = hasEncryption; return this; }
        public Builder registeredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; return this; }
        public Builder lastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder userRole(String userRole) { this.userRole = userRole; return this; }
        public Builder additionalProperty(String key, Object value) { this.additionalProperties.put(key, value); return this; }
        public Builder additionalProperties(Map<String, Object> properties) { this.additionalProperties = new HashMap<>(properties); return this; }
        
        public DeviceInfo build() {
            return new DeviceInfo(this);
        }
    }
    
    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType=" + deviceType +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", isSecure=" + isSecure() +
                ", userId='" + userId + '\'' +
                '}';
    }
    
    /**
     * Types of devices supported by the system.
     */
    public enum DeviceType {
        SMARTPHONE("Smartphone", "Mobile phone device"),
        TABLET("Tablet", "Tablet device"),
        LAPTOP("Laptop", "Laptop computer"),
        DESKTOP("Desktop", "Desktop computer"),
        KIOSK("Kiosk", "Fixed kiosk terminal"),
        UNKNOWN("Unknown", "Unknown device type");
        
        private final String displayName;
        private final String description;
        
        DeviceType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() { return displayName; }
    }
}

