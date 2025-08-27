package com.airline.ordering.security.encryption;

import java.time.LocalDateTime;

/**
 * Metadata information about an encryption key.
 */
public class KeyMetadata {
    
    private final String keyId;
    private final String algorithm;
    private final int keySize;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final KeyStatus status;
    private final String purpose;
    private final int version;
    
    public KeyMetadata(String keyId, String algorithm, int keySize, 
                      LocalDateTime createdAt, LocalDateTime expiresAt,
                      KeyStatus status, String purpose, int version) {
        this.keyId = keyId;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.purpose = purpose;
        this.version = version;
    }
    
    // Getters
    public String getKeyId() {
        return keyId;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public int getKeySize() {
        return keySize;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public KeyStatus getStatus() {
        return status;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public int getVersion() {
        return version;
    }
    
    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isActive() {
        return status == KeyStatus.ACTIVE && !isExpired();
    }
    
    public boolean isRetired() {
        return status == KeyStatus.RETIRED || isExpired();
    }
    
    public long getDaysUntilExpiry() {
        if (expiresAt == null) {
            return Long.MAX_VALUE;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toDays();
    }
    
    @Override
    public String toString() {
        return "KeyMetadata{" +
                "keyId='" + keyId + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", keySize=" + keySize +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", status=" + status +
                ", purpose='" + purpose + '\'' +
                ", version=" + version +
                '}';
    }
    
    /**
     * Status of an encryption key.
     */
    public enum KeyStatus {
        ACTIVE("Active", "Key is active and can be used for encryption/decryption"),
        RETIRED("Retired", "Key is retired but can still be used for decryption"),
        REVOKED("Revoked", "Key has been revoked and should not be used"),
        PENDING("Pending", "Key is being generated or activated"),
        EXPIRED("Expired", "Key has expired and should not be used");
        
        private final String displayName;
        private final String description;
        
        KeyStatus(String displayName, String description) {
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

