package com.taskforge.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository contract for CRUD operations.
 *
 * @param <T>  Entity type
 * @param <ID> Identifier type
 */
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    boolean existsById(ID id);
    long count();
}
