package com.taskforge.cli;

import com.taskforge.model.Role;
import com.taskforge.model.TaskPriority;
import com.taskforge.model.TaskStatus;
import com.taskforge.repository.AuditRepository;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import com.taskforge.service.AuditService;
import com.taskforge.service.AuthService;
import com.taskforge.service.ExportService;
import com.taskforge.service.ProjectService;
import com.taskforge.service.TaskService;

/**
 * Application Entry Point.
 * Sets up dependency injection and seeds initial realistic data.
 */
public class Main {
    public static void main(String[] args) {
        // Repositories
        UserRepository userRepository = new UserRepository();
        ProjectRepository projectRepository = new ProjectRepository();
        TaskRepository taskRepository = new TaskRepository();
        AuditRepository auditRepository = new AuditRepository();

        // Services
        AuditService auditService = new AuditService(auditRepository);
        AuthService authService = new AuthService(userRepository, auditService);
        ProjectService projectService = new ProjectService(projectRepository, auditService);
        TaskService taskService = new TaskService(taskRepository, auditService);
        ExportService exportService = new ExportService(taskRepository);

        // Seed demo data for realistic hands-on testing
        seedInitialData(authService, projectService, taskService);

        // Run UI
        ConsoleApp app = new ConsoleApp(authService, projectService, taskService, auditService, exportService);
        app.start();
    }

    private static void seedInitialData(AuthService authService,
                                        ProjectService projectService,
                                        TaskService taskService) {
        // Seed default users
        var admin = authService.register("admin", "admin@taskforge.dev", "admin123", Role.ADMIN);
        var dev = authService.register("alex", "alex@taskforge.dev", "password", Role.DEVELOPER);
        authService.register("sara", "sara@taskforge.dev", "password", Role.QA);

        // Seed sample project
        var project = projectService.createProject(
                "GitLearningPlatform",
                "Educational suite to help developers master Git workflows and commands",
                admin.getId()
        );
        projectService.addMember(project.getId(), dev.getId(), admin.getId());

        // Seed tasks
        var t1 = taskService.createTask(
                project.getId(),
                "Implement Git Branching Exercise",
                "Create interactive challenges for branching, switching, and merging",
                TaskPriority.HIGH,
                "admin"
        );
        taskService.assignTask(t1.getId(), "alex", "admin");
        taskService.updateStatus(t1.getId(), TaskStatus.IN_PROGRESS, "alex");
        taskService.addTag(t1.getId(), "git", "alex");
        taskService.addTag(t1.getId(), "tutorial", "alex");

        var t2 = taskService.createTask(
                project.getId(),
                "Resolve Merge Conflicts Simulator",
                "Build sample files designed to produce clean three-way merge conflicts",
                TaskPriority.CRITICAL,
                "admin"
        );
        taskService.assignTask(t2.getId(), "alex", "admin");
        taskService.addTag(t2.getId(), "conflicts", "alex");

        var t3 = taskService.createTask(
                project.getId(),
                "Audit Logging for Security Events",
                "Ensure every user authentication and task modification is auditable",
                TaskPriority.MEDIUM,
                "admin"
        );
        taskService.updateStatus(t3.getId(), TaskStatus.DONE, "admin");
    }
}
