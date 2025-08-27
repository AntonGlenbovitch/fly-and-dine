package com.airline.ordering.security.audit;

import com.airline.ordering.security.SecurityStubs.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for logging and managing audit trails.
 * Provides comprehensive audit logging for security, compliance, and debugging.
 */
public interface AuditService {
    
    /**
     * Logs an audit event.
     * 
     * @param event the audit event to log
     * @return the unique ID of the logged event
     */
    String logEvent(AuditEvent event);
    
    /**
     * Logs a security event with automatic classification.
     * 
     * @param userId the user involved in the event
     * @param action the action performed
     * @param resource the resource affected
     * @param outcome the outcome of the action
     * @param details additional event details
     * @return the unique ID of the logged event
     */
    String logSecurityEvent(String userId, String action, String resource, 
                           AuditOutcome outcome, Map<String, Object> details);
    
    /**
     * Logs a data access event for PII protection compliance.
     * 
     * @param userId the user who accessed the data
     * @param dataType the type of data accessed
     * @param recordId the ID of the record accessed
     * @param accessType the type of access (read, write, delete)
     * @param purpose the business purpose for the access
     * @return the unique ID of the logged event
     */
    String logDataAccess(String userId, String dataType, String recordId, 
                        String accessType, String purpose);
    
    /**
     * Logs an authentication event.
     * 
     * @param deviceId the device involved
     * @param userId the user involved (if known)
     * @param authType the type of authentication
     * @param outcome the outcome of the authentication
     * @param ipAddress the IP address of the request
     * @param userAgent the user agent string
     * @return the unique ID of the logged event
     */
    String logAuthenticationEvent(String deviceId, String userId, String authType,
                                 AuditOutcome outcome, String ipAddress, String userAgent);
    
    /**
     * Logs a system event.
     * 
     * @param component the system component
     * @param event the event that occurred
     * @param severity the severity level
     * @param details additional event details
     * @return the unique ID of the logged event
     */
    String logSystemEvent(String component, String event, AuditSeverity severity,
                         Map<String, Object> details);
    
    /**
     * Retrieves audit events based on criteria.
     * 
     * @param criteria the search criteria
     * @return list of matching audit events
     */
    List<AuditEvent> getEvents(AuditSearchCriteria criteria);
    
    /**
     * Retrieves audit events for a specific user.
     * 
     * @param userId the user ID
     * @param since the start date for the search
     * @param until the end date for the search
     * @return list of audit events for the user
     */
    List<AuditEvent> getUserEvents(String userId, LocalDateTime since, LocalDateTime until);
    
    /**
     * Retrieves audit events for a specific resource.
     * 
     * @param resource the resource name
     * @param since the start date for the search
     * @param until the end date for the search
     * @return list of audit events for the resource
     */
    List<AuditEvent> getResourceEvents(String resource, LocalDateTime since, LocalDateTime until);
    
    /**
     * Retrieves security events that may indicate threats.
     * 
     * @param since the start date for the search
     * @param severityThreshold the minimum severity level
     * @return list of security events
     */
    List<AuditEvent> getSecurityEvents(LocalDateTime since, AuditSeverity severityThreshold);
    
    /**
     * Generates an audit report for a specific time period.
     * 
     * @param since the start date for the report
     * @param until the end date for the report
     * @param reportType the type of report to generate
     * @return the generated audit report
     */
    AuditReport generateReport(LocalDateTime since, LocalDateTime until, AuditReportType reportType);
    
    /**
     * Exports audit events to a file for external analysis.
     * 
     * @param criteria the search criteria for events to export
     * @param format the export format
     * @param filePath the path where the export file should be saved
     * @return true if export was successful, false otherwise
     */
    boolean exportEvents(AuditSearchCriteria criteria, ExportFormat format, String filePath);
    
    /**
     * Archives old audit events to long-term storage.
     * 
     * @param olderThan the cutoff date for archiving
     * @return the number of events archived
     */
    int archiveEvents(LocalDateTime olderThan);
    
    /**
     * Purges archived audit events that are beyond retention period.
     * 
     * @param olderThan the cutoff date for purging
     * @return the number of events purged
     */
    int purgeEvents(LocalDateTime olderThan);
    
    /**
     * Validates the integrity of the audit log.
     * 
     * @return AuditIntegrityResult containing validation results
     */
    AuditIntegrityResult validateIntegrity();
    
    /**
     * Creates a tamper-evident seal for audit events.
     * 
     * @param eventIds the IDs of events to seal
     * @return the seal information
     */
    AuditSeal createSeal(List<String> eventIds);
    
    /**
     * Verifies a tamper-evident seal.
     * 
     * @param seal the seal to verify
     * @return true if the seal is valid, false otherwise
     */
    boolean verifySeal(AuditSeal seal);
    
    /**
     * Gets statistics about audit events.
     * 
     * @param since the start date for statistics
     * @param until the end date for statistics
     * @return audit statistics
     */
    AuditStatistics getStatistics(LocalDateTime since, LocalDateTime until);
    
    /**
     * Configures audit retention policies.
     * 
     * @param policy the retention policy to apply
     * @return true if configuration was successful, false otherwise
     */
    boolean configureRetentionPolicy(AuditRetentionPolicy policy);
    
    /**
     * Gets the current audit configuration.
     * 
     * @return the current audit configuration
     */
    AuditConfiguration getConfiguration();
    
    /**
     * Performs a health check on the audit system.
     * 
     * @return the health status of the audit system
     */
    AuditHealthStatus getHealthStatus();
}

