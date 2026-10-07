package com.taskforge.repository;

import com.taskforge.model.User;

import java.util.Optional;

/**
 * Repository interface and in-memory store for User entities.
 */
public class UserRepository extends InMemoryRepository<User, String> {

    public UserRepository() {
        super(User::getId);
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return storage.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }
}
