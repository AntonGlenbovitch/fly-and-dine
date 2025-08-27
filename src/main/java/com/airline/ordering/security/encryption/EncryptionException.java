package com.airline.ordering.security.encryption;

/**
 * Exception thrown when encryption or decryption operations fail.
 */
public class EncryptionException extends Exception {
    
    private final String operation;
    private final String keyId;
    
    public EncryptionException(String message) {
        super(message);
        this.operation = null;
        this.keyId = null;
    }
    
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.keyId = null;
    }
    
    public EncryptionException(String message, String operation, String keyId) {
        super(message);
        this.operation = operation;
        this.keyId = keyId;
    }
    
    public EncryptionException(String message, String operation, String keyId, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.keyId = keyId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EncryptionException: ").append(getMessage());
        
        if (operation != null) {
            sb.append(" (operation: ").append(operation).append(")");
        }
        
        if (keyId != null) {
            sb.append(" (keyId: ").append(keyId).append(")");
        }
        
        return sb.toString();
    }
}

