package com.taskforge.repository;

import com.taskforge.model.AuditLog;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory repository for AuditLog events.
 */
public class AuditRepository extends InMemoryRepository<AuditLog, String> {

    public AuditRepository() {
        super(AuditLog::getId);
    }

    public List<AuditLog> findRecent(int limit) {
        return storage.values().stream()
                .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<AuditLog> findByAction(String action) {
        if (action == null) return List.of();
        return storage.values().stream()
                .filter(a -> a.getAction().equalsIgnoreCase(action.trim()))
                .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
