package com.airline.ordering.sync;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the result of a data validation operation.
 */
public class ValidationResult {
    
    private final boolean valid;
    private final LocalDateTime timestamp;
    private final List<ValidationError> errors;
    private final List<ValidationWarning> warnings;
    private final int totalRecordsChecked;
    private final String message;
    
    private ValidationResult(Builder builder) {
        this.valid = builder.valid;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.errors = new ArrayList<>(builder.errors);
        this.warnings = new ArrayList<>(builder.warnings);
        this.totalRecordsChecked = builder.totalRecordsChecked;
        this.message = builder.message;
    }
    
    // Getters
    public boolean isValid() {
        return valid;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public List<ValidationWarning> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    public int getTotalRecordsChecked() {
        return totalRecordsChecked;
    }
    
    public String getMessage() {
        return message;
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
    public static ValidationResult valid(String message) {
        return new Builder()
                .valid(true)
                .message(message)
                .build();
    }
    
    public static ValidationResult invalid(String message, List<ValidationError> errors) {
        return new Builder()
                .valid(false)
                .message(message)
                .errors(errors)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private boolean valid;
        private LocalDateTime timestamp;
        private List<ValidationError> errors = new ArrayList<>();
        private List<ValidationWarning> warnings = new ArrayList<>();
        private int totalRecordsChecked;
        private String message;
        
        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder errors(List<ValidationError> errors) {
            this.errors = new ArrayList<>(errors);
            return this;
        }
        
        public Builder addError(ValidationError error) {
            this.errors.add(error);
            return this;
        }
        
        public Builder warnings(List<ValidationWarning> warnings) {
            this.warnings = new ArrayList<>(warnings);
            return this;
        }
        
        public Builder addWarning(ValidationWarning warning) {
            this.warnings.add(warning);
            return this;
        }
        
        public Builder totalRecordsChecked(int totalRecordsChecked) {
            this.totalRecordsChecked = totalRecordsChecked;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public ValidationResult build() {
            return new ValidationResult(this);
        }
    }
    
    /**
     * Gets a summary of the validation result.
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Validation: ");
        
        if (valid) {
            summary.append("PASSED");
        } else {
            summary.append("FAILED");
        }
        
        summary.append(" (").append(totalRecordsChecked).append(" records checked)");
        
        if (hasErrors()) {
            summary.append(" - ").append(getErrorCount()).append(" errors");
        }
        
        if (hasWarnings()) {
            summary.append(" - ").append(getWarningCount()).append(" warnings");
        }
        
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", timestamp=" + timestamp +
                ", errorCount=" + getErrorCount() +
                ", warningCount=" + getWarningCount() +
                ", totalRecordsChecked=" + totalRecordsChecked +
                ", message='" + message + '\'' +
                '}';
    }
    
    /**
     * Represents a validation error.
     */
    public static class ValidationError {
        private final UUID recordId;
        private final String field;
        private final String errorCode;
        private final String message;
        private final Object invalidValue;
        
        public ValidationError(UUID recordId, String field, String errorCode, String message, Object invalidValue) {
            this.recordId = recordId;
            this.field = field;
            this.errorCode = errorCode;
            this.message = message;
            this.invalidValue = invalidValue;
        }
        
        // Getters
        public UUID getRecordId() { return recordId; }
        public String getField() { return field; }
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public Object getInvalidValue() { return invalidValue; }
        
        @Override
        public String toString() {
            return "ValidationError{" +
                    "recordId=" + recordId +
                    ", field='" + field + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    
    /**
     * Represents a validation warning.
     */
    public static class ValidationWarning {
        private final UUID recordId;
        private final String field;
        private final String warningCode;
        private final String message;
        private final Object value;
        
        public ValidationWarning(UUID recordId, String field, String warningCode, String message, Object value) {
            this.recordId = recordId;
            this.field = field;
            this.warningCode = warningCode;
            this.message = message;
            this.value = value;
        }
        
        // Getters
        public UUID getRecordId() { return recordId; }
        public String getField() { return field; }
        public String getWarningCode() { return warningCode; }
        public String getMessage() { return message; }
        public Object getValue() { return value; }
        
        @Override
        public String toString() {
            return "ValidationWarning{" +
                    "recordId=" + recordId +
                    ", field='" + field + '\'' +
                    ", warningCode='" + warningCode + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

