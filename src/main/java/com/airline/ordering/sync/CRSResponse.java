package com.airline.ordering.sync;

import com.airline.ordering.domain.Order;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a response from the Core Reservation System (CRS).
 */
public class CRSResponse {
    
    private final boolean success;
    private final int statusCode;
    private final String message;
    private final LocalDateTime timestamp;
    private final List<Order> orders;
    private final Map<String, Object> metadata;
    private final List<CRSError> errors;
    private final String requestId;
    private final long responseTimeMs;
    
    private CRSResponse(Builder builder) {
        this.success = builder.success;
        this.statusCode = builder.statusCode;
        this.message = builder.message;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.orders = new ArrayList<>(builder.orders);
        this.metadata = new HashMap<>(builder.metadata);
        this.errors = new ArrayList<>(builder.errors);
        this.requestId = builder.requestId;
        this.responseTimeMs = builder.responseTimeMs;
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public List<CRSError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public long getResponseTimeMs() {
        return responseTimeMs;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasOrders() {
        return !orders.isEmpty();
    }
    
    public int getOrderCount() {
        return orders.size();
    }
    
    // Convenience methods for metadata
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public String getMetadataAsString(String key) {
        Object value = metadata.get(key);
        return value != null ? value.toString() : null;
    }
    
    public Integer getMetadataAsInteger(String key) {
        Object value = metadata.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    // Static factory methods
    public static CRSResponse success(String message) {
        return new Builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .build();
    }
    
    public static CRSResponse success(String message, List<Order> orders) {
        return new Builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .orders(orders)
                .build();
    }
    
    public static CRSResponse failure(int statusCode, String message) {
        return new Builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .build();
    }
    
    public static CRSResponse failure(int statusCode, String message, List<CRSError> errors) {
        return new Builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .errors(errors)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private boolean success;
        private int statusCode;
        private String message;
        private LocalDateTime timestamp;
        private List<Order> orders = new ArrayList<>();
        private Map<String, Object> metadata = new HashMap<>();
        private List<CRSError> errors = new ArrayList<>();
        private String requestId;
        private long responseTimeMs;
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder orders(List<Order> orders) {
            this.orders = new ArrayList<>(orders);
            return this;
        }
        
        public Builder addOrder(Order order) {
            this.orders.add(order);
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = new HashMap<>(metadata);
            return this;
        }
        
        public Builder addMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public Builder errors(List<CRSError> errors) {
            this.errors = new ArrayList<>(errors);
            return this;
        }
        
        public Builder addError(CRSError error) {
            this.errors.add(error);
            return this;
        }
        
        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }
        
        public Builder responseTimeMs(long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
            return this;
        }
        
        public CRSResponse build() {
            return new CRSResponse(this);
        }
    }
    
    @Override
    public String toString() {
        return "CRSResponse{" +
                "success=" + success +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", orderCount=" + orders.size() +
                ", errorCount=" + errors.size() +
                ", requestId='" + requestId + '\'' +
                ", responseTimeMs=" + responseTimeMs +
                '}';
    }
    
    /**
     * Represents an error from the CRS.
     */
    public static class CRSError {
        private final String errorCode;
        private final String message;
        private final String field;
        private final Object rejectedValue;
        
        public CRSError(String errorCode, String message) {
            this(errorCode, message, null, null);
        }
        
        public CRSError(String errorCode, String message, String field, Object rejectedValue) {
            this.errorCode = errorCode;
            this.message = message;
            this.field = field;
            this.rejectedValue = rejectedValue;
        }
        
        // Getters
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public String getField() { return field; }
        public Object getRejectedValue() { return rejectedValue; }
        
        @Override
        public String toString() {
            return "CRSError{" +
                    "errorCode='" + errorCode + '\'' +
                    ", message='" + message + '\'' +
                    ", field='" + field + '\'' +
                    '}';
        }
    }
}

