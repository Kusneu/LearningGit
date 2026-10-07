package com.taskforge.model;

import java.time.LocalDateTime;

/**
 * Tracks system events, modifications, and security actions for auditing.
 */
public class AuditLog {
    private final String id;
    private final String action;
    private final String performedBy;
    private final String details;
    private final LocalDateTime timestamp;

    public AuditLog(String id, String action, String performedBy, String details) {
        this.id = id;
        this.action = action;
        this.performedBy = performedBy;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String action() {
        return action;
    }

    public String getAction() {
        return action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] Action='%s' By='%s' -> %s", timestamp, action, performedBy, details);
    }
}
