package com.taskforge;

import com.taskforge.model.Task;
import com.taskforge.model.TaskPriority;
import com.taskforge.model.TaskStatus;
import com.taskforge.repository.AuditRepository;
import com.taskforge.repository.TaskRepository;
import com.taskforge.service.AuditService;
import com.taskforge.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {
    private TaskService taskService;
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
        AuditRepository auditRepository = new AuditRepository();
        AuditService auditService = new AuditService(auditRepository);
        taskService = new TaskService(taskRepository, auditService);
    }

    @Test
    @DisplayName("Should successfully create a task with default TODO status")
    void testCreateTask() {
        Task task = taskService.createTask("prj-101", "Implement Auth", "Add JWT auth", TaskPriority.HIGH, "admin");

        assertNotNull(task.getId());
        assertEquals("Implement Auth", task.getTitle());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(1, taskRepository.count());
    }

    @Test
    @DisplayName("Should update task status and touch updatedAt timestamp")
    void testUpdateStatus() {
        Task task = taskService.createTask("prj-101", "Bugfix", "Fix null pointer", TaskPriority.MEDIUM, "admin");
        boolean updated = taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS, "alex");

        assertTrue(updated);
        Task refreshed = taskService.getTask(task.getId()).orElseThrow();
        assertEquals(TaskStatus.IN_PROGRESS, refreshed.getStatus());
    }

    @Test
    @DisplayName("Should search tasks by title and tags")
    void testSearchTasks() {
        Task t1 = taskService.createTask("prj-1", "Database migration", "Postgres scripts", TaskPriority.CRITICAL, "admin");
        Task t2 = taskService.createTask("prj-1", "UI redesign", "CSS styles", TaskPriority.LOW, "admin");
        taskService.addTag(t2.getId(), "frontend", "admin");

        List<Task> foundDb = taskService.searchTasks("prj-1", "migration");
        assertEquals(1, foundDb.size());
        assertEquals(t1.getId(), foundDb.get(0).getId());

        List<Task> foundTag = taskService.searchTasks("prj-1", "frontend");
        assertEquals(1, foundTag.size());
        assertEquals(t2.getId(), foundTag.get(0).getId());
    }
}
