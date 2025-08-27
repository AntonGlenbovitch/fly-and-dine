package com.airline.ordering.sync;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an error that occurred during synchronization.
 */
public class SyncError {
    
    private final UUID orderId;
    private final SyncErrorType errorType;
    private final String errorCode;
    private final String message;
    private final String details;
    private final LocalDateTime timestamp;
    private final boolean retryable;
    private final Throwable cause;
    
    public SyncError(UUID orderId, SyncErrorType errorType, String errorCode, String message) {
        this(orderId, errorType, errorCode, message, null, true, null);
    }
    
    public SyncError(UUID orderId, SyncErrorType errorType, String errorCode, String message, 
                     String details, boolean retryable, Throwable cause) {
        this.orderId = orderId;
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.retryable = retryable;
        this.cause = cause;
    }
    
    // Getters
    public UUID getOrderId() {
        return orderId;
    }
    
    public SyncErrorType getErrorType() {
        return errorType;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public boolean isRetryable() {
        return retryable;
    }
    
    public Throwable getCause() {
        return cause;
    }
    
    // Static factory methods
    public static SyncError networkError(UUID orderId, String message) {
        return new SyncError(orderId, SyncErrorType.NETWORK_ERROR, "NETWORK_001", message, null, true, null);
    }
    
    public static SyncError validationError(UUID orderId, String message, String details) {
        return new SyncError(orderId, SyncErrorType.VALIDATION_ERROR, "VALIDATION_001", message, details, false, null);
    }
    
    public static SyncError conflictError(UUID orderId, String message) {
        return new SyncError(orderId, SyncErrorType.CONFLICT_ERROR, "CONFLICT_001", message, null, false, null);
    }
    
    public static SyncError authenticationError(UUID orderId, String message) {
        return new SyncError(orderId, SyncErrorType.AUTHENTICATION_ERROR, "AUTH_001", message, null, false, null);
    }
    
    public static SyncError serverError(UUID orderId, String message, Throwable cause) {
        return new SyncError(orderId, SyncErrorType.SERVER_ERROR, "SERVER_001", message, null, true, cause);
    }
    
    public static SyncError dataError(UUID orderId, String message, String details) {
        return new SyncError(orderId, SyncErrorType.DATA_ERROR, "DATA_001", message, details, false, null);
    }
    
    public static SyncError timeoutError(UUID orderId, String message) {
        return new SyncError(orderId, SyncErrorType.TIMEOUT_ERROR, "TIMEOUT_001", message, null, true, null);
    }
    
    @Override
    public String toString() {
        return "SyncError{" +
                "orderId=" + orderId +
                ", errorType=" + errorType +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", retryable=" + retryable +
                '}';
    }
}

