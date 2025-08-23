package com.airline.ordering.service;

import com.airline.ordering.domain.Order;
import com.airline.ordering.domain.OrderItem;
import com.airline.ordering.domain.PassengerType;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for validating orders, combos, and substitutions.
 * Centralizes all business rule validation logic.
 */
public interface ValidationService {
    
    /**
     * Validates an entire order for business rules compliance.
     * 
     * @param order The order to validate
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateOrder(Order order);
    
    /**
     * Validates a single order item.
     * 
     * @param orderItem The order item to validate
     * @param passengerType The passenger type
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateOrderItem(OrderItem orderItem, PassengerType passengerType);
    
    /**
     * Validates if items can be ordered together as a combo.
     * 
     * @param itemIds List of menu item IDs
     * @param passengerType The passenger type
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateCombo(List<UUID> itemIds, PassengerType passengerType);
    
    /**
     * Validates if a substitution is allowed.
     * 
     * @param originalItemId The original item ID
     * @param substitutionItemId The substitution item ID
     * @param passengerType The passenger type
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateSubstitution(UUID originalItemId, UUID substitutionItemId, PassengerType passengerType);
    
    /**
     * Validates inventory availability for an order.
     * 
     * @param order The order to validate
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateInventoryAvailability(Order order);
    
    /**
     * Validates passenger eligibility for specific menu items.
     * 
     * @param itemIds List of menu item IDs
     * @param passengerType The passenger type
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validatePassengerEligibility(List<UUID> itemIds, PassengerType passengerType);
    
    /**
     * Validates order timing constraints (e.g., alcohol service hours).
     * 
     * @param order The order to validate
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateOrderTiming(Order order);
    
    /**
     * Validates special dietary requirements and allergen conflicts.
     * 
     * @param itemIds List of menu item IDs
     * @param allergens Set of passenger allergens
     * @param dietaryRequirements Set of dietary requirements
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validateDietaryRequirements(List<UUID> itemIds, List<String> allergens, List<String> dietaryRequirements);
    
    /**
     * Represents the result of a validation operation.
     */
    class ValidationResult {
        private final List<String> errors;
        private final List<String> warnings;
        private final boolean valid;
        
        public ValidationResult(List<String> errors, List<String> warnings) {
            this.errors = errors != null ? errors : List.of();
            this.warnings = warnings != null ? warnings : List.of();
            this.valid = this.errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public static ValidationResult valid() {
            return new ValidationResult(List.of(), List.of());
        }
        
        public static ValidationResult withErrors(List<String> errors) {
            return new ValidationResult(errors, List.of());
        }
        
        public static ValidationResult withWarnings(List<String> warnings) {
            return new ValidationResult(List.of(), warnings);
        }
        
        public static ValidationResult withErrorsAndWarnings(List<String> errors, List<String> warnings) {
            return new ValidationResult(errors, warnings);
        }
    }
}

