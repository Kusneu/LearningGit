package com.taskforge.service;

import com.taskforge.model.Task;
import com.taskforge.model.TaskPriority;
import com.taskforge.model.TaskStatus;
import com.taskforge.repository.TaskRepository;
import com.taskforge.util.Validator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing task lifecycle, assignment, search, and status updates.
 */
public class TaskService {
    private final TaskRepository taskRepository;
    private final AuditService auditService;

    public TaskService(TaskRepository taskRepository, AuditService auditService) {
        this.taskRepository = taskRepository;
        this.auditService = auditService;
    }

    public Task createTask(String projectId, String title, String description,
                           TaskPriority priority, String creatorId) {
        Validator.requireNotEmpty(projectId, "Project ID");
        Validator.requireNotEmpty(title, "Task title");

        String id = "tsk-" + UUID.randomUUID().toString().substring(0, 6);
        TaskPriority prio = (priority != null) ? priority : TaskPriority.MEDIUM;
        Task task = new Task(id, title.trim(), description, prio, projectId);

        taskRepository.save(task);
        auditService.log("TASK_CREATED", creatorId, "Created task #" + id + ": '" + title + "'");
        return task;
    }

    public Optional<Task> getTask(String taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> getTasksByProject(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getTasksByAssignee(String assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    public boolean updateStatus(String taskId, TaskStatus newStatus, String actorId) {
        Optional<Task> opt = taskRepository.findById(taskId);
        if (opt.isEmpty()) return false;

        Task task = opt.get();
        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        taskRepository.save(task);

        auditService.log("TASK_STATUS_UPDATED", actorId,
                String.format("Task #%s status changed from %s to %s", taskId, oldStatus, newStatus));
        return true;
    }

    public boolean assignTask(String taskId, String assigneeId, String actorId) {
        Optional<Task> opt = taskRepository.findById(taskId);
        if (opt.isEmpty()) return false;

        Task task = opt.get();
        task.setAssigneeId(assigneeId);
        taskRepository.save(task);

        auditService.log("TASK_REASSIGNED", actorId,
                String.format("Task #%s assigned to user '%s'", taskId, assigneeId));
        return true;
    }

    public boolean addTag(String taskId, String tag, String actorId) {
        Optional<Task> opt = taskRepository.findById(taskId);
        if (opt.isEmpty()) return false;

        Task task = opt.get();
        task.addTag(tag);
        taskRepository.save(task);

        auditService.log("TASK_TAG_ADDED", actorId, "Added tag '" + tag + "' to task #" + taskId);
        return true;
    }

    public List<Task> searchTasks(String projectId, String keyword) {
        return taskRepository.search(projectId, keyword);
    }

    public List<Task> filterByStatus(String projectId, TaskStatus status) {
        return taskRepository.findByStatus(projectId, status);
    }

    public List<Task> filterByPriority(String projectId, TaskPriority priority) {
        return taskRepository.findByPriority(projectId, priority);
    }

    public boolean deleteTask(String taskId, String actorId) {
        boolean deleted = taskRepository.deleteById(taskId);
        if (deleted) {
            auditService.log("TASK_DELETED", actorId, "Deleted task #" + taskId);
        }
        return deleted;
    }
}
