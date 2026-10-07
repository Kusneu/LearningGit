package com.taskforge.repository;

import com.taskforge.model.Task;
import com.taskforge.model.TaskPriority;
import com.taskforge.model.TaskStatus;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory repository for Task entities with search and filter capabilities.
 */
public class TaskRepository extends InMemoryRepository<Task, String> {

    public TaskRepository() {
        super(Task::getId);
    }

    public List<Task> findByProjectId(String projectId) {
        if (projectId == null) return List.of();
        return storage.values().stream()
                .filter(t -> projectId.equals(t.getProjectId()))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Task> findByAssigneeId(String assigneeId) {
        if (assigneeId == null) return List.of();
        return storage.values().stream()
                .filter(t -> assigneeId.equals(t.getAssigneeId()))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Task> findByStatus(String projectId, TaskStatus status) {
        return storage.values().stream()
                .filter(t -> (projectId == null || projectId.equals(t.getProjectId())) && t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> findByPriority(String projectId, TaskPriority priority) {
        return storage.values().stream()
                .filter(t -> (projectId == null || projectId.equals(t.getProjectId())) && t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> search(String projectId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return projectId != null ? findByProjectId(projectId) : findAll();
        }
        String lower = keyword.toLowerCase().trim();
        return storage.values().stream()
                .filter(t -> (projectId == null || projectId.equals(t.getProjectId())) &&
                        (t.getTitle().toLowerCase().contains(lower) ||
                                (t.getDescription() != null && t.getDescription().toLowerCase().contains(lower)) ||
                                t.getTags().contains(lower)))
                .collect(Collectors.toList());
    }
}
