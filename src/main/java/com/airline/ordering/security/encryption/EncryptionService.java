package com.airline.ordering.security.encryption;

import java.security.Key;
import java.util.Map;

/**
 * Service interface for encrypting and decrypting sensitive data.
 * Provides field-level encryption for PII and other sensitive information.
 */
public interface EncryptionService {
    
    /**
     * Encrypts a plain text value using the default encryption key.
     * 
     * @param plainText the text to encrypt
     * @return the encrypted text as a Base64-encoded string
     * @throws EncryptionException if encryption fails
     */
    String encrypt(String plainText) throws EncryptionException;
    
    /**
     * Encrypts a plain text value using a specific encryption key.
     * 
     * @param plainText the text to encrypt
     * @param keyId the ID of the encryption key to use
     * @return the encrypted text as a Base64-encoded string
     * @throws EncryptionException if encryption fails
     */
    String encrypt(String plainText, String keyId) throws EncryptionException;
    
    /**
     * Decrypts an encrypted value using the appropriate key.
     * 
     * @param encryptedText the encrypted text as a Base64-encoded string
     * @return the decrypted plain text
     * @throws EncryptionException if decryption fails
     */
    String decrypt(String encryptedText) throws EncryptionException;
    
    /**
     * Encrypts multiple fields in a map.
     * 
     * @param data the map containing field names and values to encrypt
     * @param fieldsToEncrypt the names of fields that should be encrypted
     * @return a new map with specified fields encrypted
     * @throws EncryptionException if encryption fails
     */
    Map<String, String> encryptFields(Map<String, String> data, String... fieldsToEncrypt) throws EncryptionException;
    
    /**
     * Decrypts multiple fields in a map.
     * 
     * @param data the map containing field names and encrypted values
     * @param fieldsToDecrypt the names of fields that should be decrypted
     * @return a new map with specified fields decrypted
     * @throws EncryptionException if decryption fails
     */
    Map<String, String> decryptFields(Map<String, String> data, String... fieldsToDecrypt) throws EncryptionException;
    
    /**
     * Generates a hash of the given value for comparison purposes.
     * 
     * @param value the value to hash
     * @return the hash as a hex string
     */
    String hash(String value);
    
    /**
     * Verifies that a value matches a given hash.
     * 
     * @param value the value to verify
     * @param hash the hash to compare against
     * @return true if the value matches the hash, false otherwise
     */
    boolean verifyHash(String value, String hash);
    
    /**
     * Generates a secure random salt for password hashing.
     * 
     * @return a random salt as a hex string
     */
    String generateSalt();
    
    /**
     * Hashes a password with a salt using a secure algorithm.
     * 
     * @param password the password to hash
     * @param salt the salt to use
     * @return the hashed password
     */
    String hashPassword(String password, String salt);
    
    /**
     * Verifies a password against a stored hash and salt.
     * 
     * @param password the password to verify
     * @param hash the stored hash
     * @param salt the stored salt
     * @return true if the password is correct, false otherwise
     */
    boolean verifyPassword(String password, String hash, String salt);
    
    /**
     * Masks sensitive data for logging or display purposes.
     * 
     * @param value the value to mask
     * @param maskChar the character to use for masking
     * @param visibleChars the number of characters to leave visible at the end
     * @return the masked value
     */
    String maskValue(String value, char maskChar, int visibleChars);
    
    /**
     * Generates a new encryption key and stores it with the given ID.
     * 
     * @param keyId the ID to assign to the new key
     * @return true if the key was generated successfully, false otherwise
     */
    boolean generateKey(String keyId);
    
    /**
     * Rotates an encryption key by generating a new key and marking the old one for retirement.
     * 
     * @param keyId the ID of the key to rotate
     * @return true if the key was rotated successfully, false otherwise
     */
    boolean rotateKey(String keyId);
    
    /**
     * Gets information about available encryption keys.
     * 
     * @return a map of key IDs to key metadata
     */
    Map<String, KeyMetadata> getKeyInfo();
    
    /**
     * Checks if the encryption service is properly initialized and ready to use.
     * 
     * @return true if the service is ready, false otherwise
     */
    boolean isReady();
    
    /**
     * Performs a self-test to verify encryption/decryption functionality.
     * 
     * @return true if the self-test passes, false otherwise
     */
    boolean selfTest();
}

