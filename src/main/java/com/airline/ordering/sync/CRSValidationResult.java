package com.airline.ordering.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the result of validating an order against CRS business rules.
 */
public class CRSValidationResult {
    
    private final boolean valid;
    private final UUID orderId;
    private final List<CRSValidationError> errors;
    private final List<CRSValidationWarning> warnings;
    private final String message;
    
    public CRSValidationResult(boolean valid, UUID orderId, String message,
                              List<CRSValidationError> errors, List<CRSValidationWarning> warnings) {
        this.valid = valid;
        this.orderId = orderId;
        this.message = message;
        this.errors = new ArrayList<>(errors != null ? errors : new ArrayList<>());
        this.warnings = new ArrayList<>(warnings != null ? warnings : new ArrayList<>());
    }
    
    // Getters
    public boolean isValid() {
        return valid;
    }
    
    public UUID getOrderId() {
        return orderId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public List<CRSValidationError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public List<CRSValidationWarning> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public int getWarningCount() {
        return warnings.size();
    }
    
    // Static factory methods
    public static CRSValidationResult valid(UUID orderId, String message) {
        return new CRSValidationResult(true, orderId, message, null, null);
    }
    
    public static CRSValidationResult invalid(UUID orderId, String message, List<CRSValidationError> errors) {
        return new CRSValidationResult(false, orderId, message, errors, null);
    }
    
    public static CRSValidationResult withWarnings(UUID orderId, String message, List<CRSValidationWarning> warnings) {
        return new CRSValidationResult(true, orderId, message, null, warnings);
    }
    
    @Override
    public String toString() {
        return "CRSValidationResult{" +
                "valid=" + valid +
                ", orderId=" + orderId +
                ", message='" + message + '\'' +
                ", errorCount=" + errors.size() +
                ", warningCount=" + warnings.size() +
                '}';
    }
    
    /**
     * Represents a validation error from the CRS.
     */
    public static class CRSValidationError {
        private final String errorCode;
        private final String message;
        private final String field;
        private final Object rejectedValue;
        private final String severity;
        
        public CRSValidationError(String errorCode, String message, String field, 
                                 Object rejectedValue, String severity) {
            this.errorCode = errorCode;
            this.message = message;
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.severity = severity;
        }
        
        // Getters
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public String getField() { return field; }
        public Object getRejectedValue() { return rejectedValue; }
        public String getSeverity() { return severity; }
        
        @Override
        public String toString() {
            return "CRSValidationError{" +
                    "errorCode='" + errorCode + '\'' +
                    ", message='" + message + '\'' +
                    ", field='" + field + '\'' +
                    ", severity='" + severity + '\'' +
                    '}';
        }
    }
    
    /**
     * Represents a validation warning from the CRS.
     */
    public static class CRSValidationWarning {
        private final String warningCode;
        private final String message;
        private final String field;
        private final Object value;
        
        public CRSValidationWarning(String warningCode, String message, String field, Object value) {
            this.warningCode = warningCode;
            this.message = message;
            this.field = field;
            this.value = value;
        }
        
        // Getters
        public String getWarningCode() { return warningCode; }
        public String getMessage() { return message; }
        public String getField() { return field; }
        public Object getValue() { return value; }
        
        @Override
        public String toString() {
            return "CRSValidationWarning{" +
                    "warningCode='" + warningCode + '\'' +
                    ", message='" + message + '\'' +
                    ", field='" + field + '\'' +
                    '}';
        }
    }
}

