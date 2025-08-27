package com.airline.ordering.sync;

import java.time.LocalDateTime;

/**
 * Represents rate limiting information for the CRS API.
 */
public class CRSRateLimit {
    
    private final int requestsPerMinute;
    private final int requestsPerHour;
    private final int requestsPerDay;
    private final int remainingRequests;
    private final LocalDateTime resetTime;
    private final int burstLimit;
    private final long retryAfterSeconds;
    
    public CRSRateLimit(int requestsPerMinute, int requestsPerHour, int requestsPerDay,
                       int remainingRequests, LocalDateTime resetTime, int burstLimit,
                       long retryAfterSeconds) {
        this.requestsPerMinute = requestsPerMinute;
        this.requestsPerHour = requestsPerHour;
        this.requestsPerDay = requestsPerDay;
        this.remainingRequests = remainingRequests;
        this.resetTime = resetTime;
        this.burstLimit = burstLimit;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    // Getters
    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }
    
    public int getRequestsPerHour() {
        return requestsPerHour;
    }
    
    public int getRequestsPerDay() {
        return requestsPerDay;
    }
    
    public int getRemainingRequests() {
        return remainingRequests;
    }
    
    public LocalDateTime getResetTime() {
        return resetTime;
    }
    
    public int getBurstLimit() {
        return burstLimit;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
    
    // Utility methods
    public boolean isRateLimited() {
        return remainingRequests <= 0 || retryAfterSeconds > 0;
    }
    
    public boolean canMakeRequest() {
        return remainingRequests > 0 && retryAfterSeconds == 0;
    }
    
    public boolean canMakeBatchRequest(int batchSize) {
        return remainingRequests >= batchSize && retryAfterSeconds == 0;
    }
    
    public double getUsagePercentage() {
        if (requestsPerMinute <= 0) return 0.0;
        int usedRequests = requestsPerMinute - remainingRequests;
        return (double) usedRequests / requestsPerMinute * 100.0;
    }
    
    public boolean isNearLimit(double threshold) {
        return getUsagePercentage() >= threshold;
    }
    
    public long getSecondsUntilReset() {
        if (resetTime == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), resetTime).getSeconds();
    }
    
    // Static factory methods
    public static CRSRateLimit unlimited() {
        return new CRSRateLimit(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
                               Integer.MAX_VALUE, null, Integer.MAX_VALUE, 0);
    }
    
    public static CRSRateLimit rateLimited(long retryAfterSeconds) {
        return new CRSRateLimit(0, 0, 0, 0, 
                               LocalDateTime.now().plusSeconds(retryAfterSeconds),
                               0, retryAfterSeconds);
    }
    
    @Override
    public String toString() {
        return "CRSRateLimit{" +
                "requestsPerMinute=" + requestsPerMinute +
                ", requestsPerHour=" + requestsPerHour +
                ", requestsPerDay=" + requestsPerDay +
                ", remainingRequests=" + remainingRequests +
                ", resetTime=" + resetTime +
                ", burstLimit=" + burstLimit +
                ", retryAfterSeconds=" + retryAfterSeconds +
                ", usagePercentage=" + String.format("%.1f", getUsagePercentage()) + "%" +
                '}';
    }
}

