package com.taskforge.service;

import com.taskforge.model.Role;
import com.taskforge.model.User;
import com.taskforge.repository.UserRepository;
import com.taskforge.util.PasswordHasher;
import com.taskforge.util.Validator;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles authentication, user registration, and active session management.
 */
public class AuthService {
    private final UserRepository userRepository;
    private final AuditService auditService;
    private User currentUser;

    public AuthService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public User register(String username, String email, String password, Role role) {
        Validator.requireNotEmpty(username, "Username");
        Validator.requireNotEmpty(email, "Email");
        Validator.requireNotEmpty(password, "Password");

        if (!Validator.isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be alphanumeric and 3-20 characters long.");
        }
        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username '" + username + "' is already taken.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email '" + email + "' is already registered.");
        }

        String id = "usr-" + UUID.randomUUID().toString().substring(0, 8);
        String hash = PasswordHasher.hash(password);
        User user = new User(id, username.trim(), email.trim(), hash, role != null ? role : Role.DEVELOPER);

        userRepository.save(user);
        auditService.log("USER_REGISTERED", user.getUsername(), "Registered new user with role " + user.getRole());
        return user;
    }

    public boolean login(String username, String password) {
        Validator.requireNotEmpty(username, "Username");
        Validator.requireNotEmpty(password, "Password");

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        if (PasswordHasher.verify(password, user.getPasswordHash())) {
            this.currentUser = user;
            auditService.log("USER_LOGIN_SUCCESS", user.getUsername(), "User logged in successfully");
            return true;
        } else {
            auditService.log("USER_LOGIN_FAILED", username, "Invalid password attempt");
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            auditService.log("USER_LOGOUT", currentUser.getUsername(), "User logged out");
            this.currentUser = null;
        }
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
