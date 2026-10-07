package com.taskforge.service;

import com.taskforge.model.AuditLog;
import com.taskforge.repository.AuditRepository;

import java.util.List;
import java.util.UUID;

/**
 * Service to manage and query the audit trail.
 */
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void log(String action, String performedBy, String details) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        AuditLog auditLog = new AuditLog(id, action, performedBy != null ? performedBy : "SYSTEM", details);
        auditRepository.save(auditLog);
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditRepository.findRecent(limit);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditRepository.findByAction(action);
    }
}
