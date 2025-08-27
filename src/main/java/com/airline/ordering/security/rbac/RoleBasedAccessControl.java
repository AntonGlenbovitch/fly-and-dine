package com.airline.ordering.security.rbac;

import com.airline.ordering.security.SecurityStubs.*;
import java.util.List;
import java.util.Set;

/**
 * Role-Based Access Control (RBAC) service for managing user permissions.
 * Provides fine-grained access control based on user roles and permissions.
 */
public interface RoleBasedAccessControl {
    
    /**
     * Checks if a user has permission to perform a specific action on a resource.
     * 
     * @param userId the user identifier
     * @param resource the resource being accessed
     * @param action the action being performed
     * @return true if the user has permission, false otherwise
     */
    boolean hasPermission(String userId, String resource, String action);
    
    /**
     * Checks if a user has permission with additional context.
     * 
     * @param userId the user identifier
     * @param resource the resource being accessed
     * @param action the action being performed
     * @param context additional context for permission checking
     * @return PermissionResult containing the decision and reasoning
     */
    PermissionResult checkPermission(String userId, String resource, String action, PermissionContext context);
    
    /**
     * Gets all roles assigned to a user.
     * 
     * @param userId the user identifier
     * @return set of role names assigned to the user
     */
    Set<String> getUserRoles(String userId);
    
    /**
     * Gets all permissions for a user (aggregated from all roles).
     * 
     * @param userId the user identifier
     * @return set of permissions the user has
     */
    Set<Permission> getUserPermissions(String userId);
    
    /**
     * Assigns a role to a user.
     * 
     * @param userId the user identifier
     * @param roleName the role to assign
     * @return true if assignment was successful, false otherwise
     */
    boolean assignRole(String userId, String roleName);
    
    /**
     * Removes a role from a user.
     * 
     * @param userId the user identifier
     * @param roleName the role to remove
     * @return true if removal was successful, false otherwise
     */
    boolean removeRole(String userId, String roleName);
    
    /**
     * Creates a new role with specified permissions.
     * 
     * @param role the role to create
     * @return true if creation was successful, false otherwise
     */
    boolean createRole(Role role);
    
    /**
     * Updates an existing role's permissions.
     * 
     * @param roleName the name of the role to update
     * @param permissions the new set of permissions
     * @return true if update was successful, false otherwise
     */
    boolean updateRole(String roleName, Set<Permission> permissions);
    
    /**
     * Deletes a role and removes it from all users.
     * 
     * @param roleName the name of the role to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteRole(String roleName);
    
    /**
     * Gets information about a specific role.
     * 
     * @param roleName the name of the role
     * @return Role information if it exists, null otherwise
     */
    Role getRole(String roleName);
    
    /**
     * Lists all available roles in the system.
     * 
     * @return list of all roles
     */
    List<Role> getAllRoles();
    
    /**
     * Gets all users assigned to a specific role.
     * 
     * @param roleName the name of the role
     * @return list of user IDs assigned to the role
     */
    List<String> getUsersWithRole(String roleName);
    
    /**
     * Validates that a role hierarchy is consistent (no circular dependencies).
     * 
     * @return true if the role hierarchy is valid, false otherwise
     */
    boolean validateRoleHierarchy();
    
    /**
     * Gets effective permissions for a user considering role hierarchy.
     * 
     * @param userId the user identifier
     * @return set of effective permissions
     */
    Set<Permission> getEffectivePermissions(String userId);
    
    /**
     * Checks if a user can delegate a permission to another user.
     * 
     * @param delegatorId the user who wants to delegate
     * @param delegateeId the user who would receive the permission
     * @param permission the permission to delegate
     * @return true if delegation is allowed, false otherwise
     */
    boolean canDelegate(String delegatorId, String delegateeId, Permission permission);
    
    /**
     * Temporarily grants a permission to a user for a specific duration.
     * 
     * @param userId the user identifier
     * @param permission the permission to grant
     * @param durationMinutes the duration in minutes
     * @return true if temporary grant was successful, false otherwise
     */
    boolean grantTemporaryPermission(String userId, Permission permission, int durationMinutes);
    
    /**
     * Revokes a temporarily granted permission.
     * 
     * @param userId the user identifier
     * @param permission the permission to revoke
     * @return true if revocation was successful, false otherwise
     */
    boolean revokeTemporaryPermission(String userId, Permission permission);
    
    /**
     * Gets audit trail of permission changes for a user.
     * 
     * @param userId the user identifier
     * @param since the start date for the audit trail
     * @return list of permission audit events
     */
    List<PermissionAuditEvent> getPermissionAuditTrail(String userId, java.time.LocalDateTime since);
    
    /**
     * Performs a security audit of the RBAC system.
     * 
     * @return SecurityAuditResult containing audit findings
     */
    SecurityAuditResult performSecurityAudit();
}

