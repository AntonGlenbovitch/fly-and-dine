package com.airline.ordering.sync;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents authentication credentials for the Core Reservation System (CRS).
 */
public class CRSCredentials {
    
    private final String username;
    private final String password;
    private final String apiKey;
    private final String token;
    private final LocalDateTime tokenExpiry;
    private final String clientId;
    private final String clientSecret;
    private final Map<String, String> additionalProperties;
    
    private CRSCredentials(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.apiKey = builder.apiKey;
        this.token = builder.token;
        this.tokenExpiry = builder.tokenExpiry;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.additionalProperties = new HashMap<>(builder.additionalProperties);
    }
    
    // Getters
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getToken() {
        return token;
    }
    
    public LocalDateTime getTokenExpiry() {
        return tokenExpiry;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public Map<String, String> getAdditionalProperties() {
        return new HashMap<>(additionalProperties);
    }
    
    public String getAdditionalProperty(String key) {
        return additionalProperties.get(key);
    }
    
    // Validation methods
    public boolean hasUsernamePassword() {
        return username != null && !username.trim().isEmpty() && 
               password != null && !password.trim().isEmpty();
    }
    
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
    
    public boolean hasToken() {
        return token != null && !token.trim().isEmpty();
    }
    
    public boolean hasClientCredentials() {
        return clientId != null && !clientId.trim().isEmpty() && 
               clientSecret != null && !clientSecret.trim().isEmpty();
    }
    
    public boolean isTokenExpired() {
        return tokenExpiry != null && LocalDateTime.now().isAfter(tokenExpiry);
    }
    
    public boolean isTokenValid() {
        return hasToken() && !isTokenExpired();
    }
    
    public boolean isValid() {
        return hasUsernamePassword() || hasApiKey() || isTokenValid() || hasClientCredentials();
    }
    
    // Static factory methods
    public static CRSCredentials usernamePassword(String username, String password) {
        return new Builder()
                .username(username)
                .password(password)
                .build();
    }
    
    public static CRSCredentials apiKey(String apiKey) {
        return new Builder()
                .apiKey(apiKey)
                .build();
    }
    
    public static CRSCredentials token(String token, LocalDateTime expiry) {
        return new Builder()
                .token(token)
                .tokenExpiry(expiry)
                .build();
    }
    
    public static CRSCredentials clientCredentials(String clientId, String clientSecret) {
        return new Builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private String username;
        private String password;
        private String apiKey;
        private String token;
        private LocalDateTime tokenExpiry;
        private String clientId;
        private String clientSecret;
        private Map<String, String> additionalProperties = new HashMap<>();
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        
        public Builder tokenExpiry(LocalDateTime tokenExpiry) {
            this.tokenExpiry = tokenExpiry;
            return this;
        }
        
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        
        public Builder additionalProperty(String key, String value) {
            this.additionalProperties.put(key, value);
            return this;
        }
        
        public Builder additionalProperties(Map<String, String> properties) {
            this.additionalProperties = new HashMap<>(properties);
            return this;
        }
        
        public CRSCredentials build() {
            return new CRSCredentials(this);
        }
    }
    
    /**
     * Creates a copy of these credentials with a new token.
     */
    public CRSCredentials withToken(String newToken, LocalDateTime newExpiry) {
        return new Builder()
                .username(this.username)
                .password(this.password)
                .apiKey(this.apiKey)
                .token(newToken)
                .tokenExpiry(newExpiry)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .additionalProperties(this.additionalProperties)
                .build();
    }
    
    @Override
    public String toString() {
        return "CRSCredentials{" +
                "username='" + (username != null ? "***" : null) + '\'' +
                ", hasPassword=" + (password != null) +
                ", hasApiKey=" + (apiKey != null) +
                ", hasToken=" + (token != null) +
                ", tokenExpiry=" + tokenExpiry +
                ", clientId='" + clientId + '\'' +
                ", hasClientSecret=" + (clientSecret != null) +
                '}';
    }
}

