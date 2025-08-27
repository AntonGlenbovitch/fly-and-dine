package com.airline.ordering.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic repository interface for common CRUD operations.
 * @param <T> The type of the entity
 * @param <ID> The type of the entity's ID
 */
public interface Repository<T, ID> {
    
    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     * @param entity Must not be {@literal null}.
     * @return The saved entity; will never be {@literal null}.
     */
    T save(T entity);
    
    /**
     * Retrieves an entity by its id.
     * @param id Must not be {@literal null}.
     * @return Optional containing the entity with the given id or {@literal Optional#empty()} if not found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    Optional<T> findById(ID id);
    
    /**
     * Returns all instances of the type.
     * @return All entities.
     */
    List<T> findAll();
    
    /**
     * Deletes the entity with the given id.
     * @param id Must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}.
     */
    void deleteById(ID id);
    
    /**
     * Deletes a given entity.
     * @param entity Must not be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void delete(T entity);
    
    /**
     * Returns the number of entities available.
     * @return The number of entities.
     */
    long count();
}

