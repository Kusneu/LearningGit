package com.taskforge;

import com.taskforge.model.Role;
import com.taskforge.model.User;
import com.taskforge.repository.AuditRepository;
import com.taskforge.repository.UserRepository;
import com.taskforge.service.AuditService;
import com.taskforge.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        AuditRepository auditRepository = new AuditRepository();
        AuditService auditService = new AuditService(auditRepository);
        authService = new AuthService(userRepository, auditService);
    }

    @Test
    @DisplayName("Should successfully register a new user and prevent duplicate username")
    void testRegisterUser() {
        User user = authService.register("john_doe", "john@example.com", "secret123", Role.DEVELOPER);

        assertNotNull(user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());

        assertThrows(IllegalStateException.class, () ->
                authService.register("john_doe", "other@example.com", "password", Role.QA));
    }

    @Test
    @DisplayName("Should allow login with correct credentials and reject invalid ones")
    void testLoginFlow() {
        authService.register("alice", "alice@example.com", "mypassword", Role.MANAGER);

        assertTrue(authService.login("alice", "mypassword"));
        assertTrue(authService.isAuthenticated());
        assertEquals("alice", authService.getCurrentUser().orElseThrow().getUsername());

        authService.logout();
        assertFalse(authService.isAuthenticated());

        assertFalse(authService.login("alice", "wrongpassword"));
        assertFalse(authService.login("nonexistent", "mypassword"));
    }
}
