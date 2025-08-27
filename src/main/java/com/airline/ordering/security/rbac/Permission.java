package com.airline.ordering.security.rbac;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a permission in the RBAC system.
 * Permissions define what actions can be performed on specific resources.
 */
public class Permission {
    
    private final String resource;
    private final String action;
    private final Set<String> constraints;
    private final PermissionScope scope;
    private final String description;
    
    public Permission(String resource, String action) {
        this(resource, action, PermissionScope.GLOBAL, null, new HashSet<>());
    }
    
    public Permission(String resource, String action, PermissionScope scope) {
        this(resource, action, scope, null, new HashSet<>());
    }
    
    public Permission(String resource, String action, PermissionScope scope, String description) {
        this(resource, action, scope, description, new HashSet<>());
    }
    
    public Permission(String resource, String action, PermissionScope scope, String description, Set<String> constraints) {
        this.resource = resource;
        this.action = action;
        this.scope = scope != null ? scope : PermissionScope.GLOBAL;
        this.description = description;
        this.constraints = new HashSet<>(constraints != null ? constraints : new HashSet<>());
    }
    
    // Getters
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public Set<String> getConstraints() { return new HashSet<>(constraints); }
    public PermissionScope getScope() { return scope; }
    public String getDescription() { return description; }
    
    // Utility methods
    public String getPermissionString() {
        return resource + ":" + action;
    }
    
    public boolean hasConstraint(String constraint) {
        return constraints.contains(constraint);
    }
    
    public boolean isGlobal() {
        return scope == PermissionScope.GLOBAL;
    }
    
    public boolean isRestricted() {
        return scope == PermissionScope.RESTRICTED || !constraints.isEmpty();
    }
    
    public boolean matches(String resource, String action) {
        return this.resource.equals(resource) && this.action.equals(action);
    }
    
    public boolean matchesPattern(String resourcePattern, String actionPattern) {
        return matchesWildcard(this.resource, resourcePattern) && 
               matchesWildcard(this.action, actionPattern);
    }
    
    private boolean matchesWildcard(String value, String pattern) {
        if (pattern.equals("*")) return true;
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return value.startsWith(prefix);
        }
        return value.equals(pattern);
    }
    
    // Static factory methods for common permissions
    public static Permission read(String resource) {
        return new Permission(resource, "read", PermissionScope.GLOBAL, "Read access to " + resource);
    }
    
    public static Permission write(String resource) {
        return new Permission(resource, "write", PermissionScope.GLOBAL, "Write access to " + resource);
    }
    
    public static Permission delete(String resource) {
        return new Permission(resource, "delete", PermissionScope.RESTRICTED, "Delete access to " + resource);
    }
    
    public static Permission admin(String resource) {
        return new Permission(resource, "admin", PermissionScope.RESTRICTED, "Administrative access to " + resource);
    }
    
    public static Permission all(String resource) {
        return new Permission(resource, "*", PermissionScope.GLOBAL, "Full access to " + resource);
    }
    
    // Common application permissions
    public static class Orders {
        public static final Permission VIEW = read("orders");
        public static final Permission CREATE = new Permission("orders", "create");
        public static final Permission MODIFY = write("orders");
        public static final Permission CANCEL = new Permission("orders", "cancel");
        public static final Permission CONFIRM = new Permission("orders", "confirm");
        public static final Permission VIEW_ALL = new Permission("orders", "view_all", PermissionScope.RESTRICTED);
    }
    
    public static class Menu {
        public static final Permission VIEW = read("menu");
        public static final Permission MODIFY = write("menu");
        public static final Permission MANAGE_INVENTORY = new Permission("menu", "manage_inventory", PermissionScope.RESTRICTED);
    }
    
    public static class Passengers {
        public static final Permission VIEW = read("passengers");
        public static final Permission VIEW_PII = new Permission("passengers", "view_pii", PermissionScope.RESTRICTED);
        public static final Permission MODIFY = write("passengers");
    }
    
    public static class System {
        public static final Permission ADMIN = admin("system");
        public static final Permission AUDIT = new Permission("system", "audit", PermissionScope.RESTRICTED);
        public static final Permission BACKUP = new Permission("system", "backup", PermissionScope.RESTRICTED);
        public static final Permission SYNC = new Permission("system", "sync");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(resource, that.resource) &&
               Objects.equals(action, that.action) &&
               Objects.equals(scope, that.scope) &&
               Objects.equals(constraints, that.constraints);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(resource, action, scope, constraints);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPermissionString());
        
        if (scope != PermissionScope.GLOBAL) {
            sb.append(" (").append(scope).append(")");
        }
        
        if (!constraints.isEmpty()) {
            sb.append(" [").append(String.join(", ", constraints)).append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * Scope of a permission defining its reach and restrictions.
     */
    public enum PermissionScope {
        GLOBAL("Global", "Permission applies globally without restrictions"),
        RESTRICTED("Restricted", "Permission has specific restrictions or conditions"),
        CONTEXTUAL("Contextual", "Permission depends on specific context or conditions"),
        TEMPORARY("Temporary", "Permission is granted temporarily");
        
        private final String displayName;
        private final String description;
        
        PermissionScope(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() { return displayName; }
    }
}

