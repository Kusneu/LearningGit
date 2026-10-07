package com.taskforge.cli;

import com.taskforge.model.*;
import com.taskforge.service.*;
import com.taskforge.util.AnsiColor;
import com.taskforge.util.DateTimeUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interactive command-line user interface for TaskForge.
 */
public class ConsoleApp {
    private final AuthService authService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final AuditService auditService;
    private final ExportService exportService;
    private final Scanner scanner;

    private Project activeProject;

    public ConsoleApp(AuthService authService,
                      ProjectService projectService,
                      TaskService taskService,
                      AuditService auditService,
                      ExportService exportService) {
        this.authService = authService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.auditService = auditService;
        this.exportService = exportService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            if (!authService.isAuthenticated()) {
                running = handleAuthMenu();
            } else {
                running = handleMainMenu();
            }
        }
        System.out.println(AnsiColor.cyan("\nGoodbye! Thanks for using TaskForge."));
    }

    private void printBanner() {
        System.out.println(AnsiColor.cyan("======================================================"));
        System.out.println(AnsiColor.bold("   ⚡ TaskForge: Developer Project & Task Tracker ⚡  "));
        System.out.println(AnsiColor.cyan("======================================================"));
    }

    private boolean handleAuthMenu() {
        System.out.println("\n" + AnsiColor.bold("--- Authentication ---"));
        System.out.println("1. Login");
        System.out.println("2. Register new account");
        System.out.println("3. Exit");
        System.out.print(AnsiColor.yellow("Select option [1-3]: "));

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> loginPrompt();
            case "2" -> registerPrompt();
            case "3" -> { return false; }
            default -> System.out.println(AnsiColor.red("Invalid option. Please try again."));
        }
        return true;
    }

    private void loginPrompt() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (authService.login(username, password)) {
            User user = authService.getCurrentUser().orElseThrow();
            System.out.println(AnsiColor.green("✓ Welcome back, " + user.getUsername() + "! (Role: " + user.getRole() + ")"));
            // Auto-select first available project if any
            List<Project> projects = projectService.listProjectsForUser(user.getId());
            if (!projects.isEmpty()) {
                activeProject = projects.get(0);
                System.out.println(AnsiColor.cyan("Active project set to: " + activeProject.getName()));
            }
        } else {
            System.out.println(AnsiColor.red("✗ Invalid username or password."));
        }
    }

    private void registerPrompt() {
        System.out.print("Desired username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = authService.register(username, email, password, Role.DEVELOPER);
            System.out.println(AnsiColor.green("✓ Registered successfully as " + user.getUsername() + ". Please login."));
        } catch (Exception e) {
            System.out.println(AnsiColor.red("✗ Registration failed: " + e.getMessage()));
        }
    }

    private boolean handleMainMenu() {
        User user = authService.getCurrentUser().orElseThrow();
        String projStr = (activeProject != null) ? activeProject.getName() : "None";

        System.out.println("\n" + AnsiColor.bold("--- TaskForge Dashboard ---"));
        System.out.println("Logged in as: " + AnsiColor.cyan(user.getUsername()) +
                " | Active Project: " + AnsiColor.yellow(projStr));
        System.out.println("1. List All Tasks in Active Project");
        System.out.println("2. Create New Task");
        System.out.println("3. Update Task Status");
        System.out.println("4. Assign Task to User");
        System.out.println("5. Search Tasks");
        System.out.println("6. Switch / Create Project");
        System.out.println("7. Export Project Tasks (Markdown / CSV)");
        System.out.println("8. View Audit Trail");
        System.out.println("9. Logout");
        System.out.println("0. Exit");
        System.out.print(AnsiColor.yellow("Enter command [0-9]: "));

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> listTasks();
            case "2" -> createTask();
            case "3" -> updateTaskStatus();
            case "4" -> assignTask();
            case "5" -> searchTasks();
            case "6" -> manageProjects();
            case "7" -> exportTasks();
            case "8" -> viewAuditLog();
            case "9" -> {
                authService.logout();
                activeProject = null;
                System.out.println(AnsiColor.yellow("Logged out successfully."));
            }
            case "0" -> { return false; }
            default -> System.out.println(AnsiColor.red("Invalid option."));
        }
        return true;
    }

    private void listTasks() {
        if (activeProject == null) {
            System.out.println(AnsiColor.red("No active project selected. Please select a project first."));
            return;
        }

        List<Task> tasks = taskService.getTasksByProject(activeProject.getId());
        if (tasks.isEmpty()) {
            System.out.println(AnsiColor.yellow("No tasks found in project '" + activeProject.getName() + "'."));
            return;
        }

        System.out.println("\n" + AnsiColor.bold("Tasks in " + activeProject.getName() + ":"));
        for (Task t : tasks) {
            printTaskBadge(t);
        }
    }

    private void printTaskBadge(Task t) {
        String statusColor = switch (t.getStatus()) {
            case TODO -> AnsiColor.WHITE;
            case IN_PROGRESS -> AnsiColor.BLUE;
            case IN_REVIEW -> AnsiColor.PURPLE;
            case DONE -> AnsiColor.GREEN;
            case BLOCKED -> AnsiColor.RED;
        };

        String priorityColor = switch (t.getPriority()) {
            case LOW -> AnsiColor.DIM;
            case MEDIUM -> AnsiColor.WHITE;
            case HIGH -> AnsiColor.YELLOW;
            case CRITICAL -> AnsiColor.RED;
        };

        System.out.printf("[%s] %s | Prio: %s | Status: %s | Assignee: %s\n",
                t.getId(),
                AnsiColor.bold(t.getTitle()),
                AnsiColor.color(t.getPriority().name(), priorityColor),
                AnsiColor.color(t.getStatus().getDisplayName(), statusColor),
                t.getAssigneeId() != null ? t.getAssigneeId() : "Unassigned");

        if (t.getDescription() != null && !t.getDescription().isBlank()) {
            System.out.println("   └ " + t.getDescription());
        }
    }

    private void createTask() {
        if (activeProject == null) {
            System.out.println(AnsiColor.red("Please select an active project first."));
            return;
        }

        System.out.print("Task Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Task Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Priority (LOW, MEDIUM, HIGH, CRITICAL) [default: MEDIUM]: ");
        String prioInput = scanner.nextLine().trim().toUpperCase();

        TaskPriority priority = TaskPriority.MEDIUM;
        if (!prioInput.isBlank()) {
            try {
                priority = TaskPriority.valueOf(prioInput);
            } catch (IllegalArgumentException e) {
                System.out.println(AnsiColor.yellow("Invalid priority. Defaulting to MEDIUM."));
            }
        }

        User user = authService.getCurrentUser().orElseThrow();
        Task created = taskService.createTask(activeProject.getId(), title, description, priority, user.getUsername());
        System.out.println(AnsiColor.green("✓ Task created successfully with ID: " + created.getId()));
    }

    private void updateTaskStatus() {
        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();

        Optional<Task> opt = taskService.getTask(taskId);
        if (opt.isEmpty()) {
            System.out.println(AnsiColor.red("Task not found."));
            return;
        }

        System.out.println("Available Statuses: TODO, IN_PROGRESS, IN_REVIEW, DONE, BLOCKED");
        System.out.print("New Status: ");
        String statusInput = scanner.nextLine().trim().toUpperCase();

        try {
            TaskStatus newStatus = TaskStatus.valueOf(statusInput);
            User user = authService.getCurrentUser().orElseThrow();
            taskService.updateStatus(taskId, newStatus, user.getUsername());
            System.out.println(AnsiColor.green("✓ Task status updated to " + newStatus.getDisplayName()));
        } catch (IllegalArgumentException e) {
            System.out.println(AnsiColor.red("Invalid status value."));
        }
    }

    private void assignTask() {
        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();
        System.out.print("Assign to Username: ");
        String username = scanner.nextLine().trim();

        User user = authService.getCurrentUser().orElseThrow();
        boolean ok = taskService.assignTask(taskId, username, user.getUsername());
        if (ok) {
            System.out.println(AnsiColor.green("✓ Task assigned to " + username));
        } else {
            System.out.println(AnsiColor.red("Task not found."));
        }
    }

    private void searchTasks() {
        String projId = (activeProject != null) ? activeProject.getId() : null;
        System.out.print("Enter search keyword (title/description/tag): ");
        String query = scanner.nextLine().trim();

        List<Task> results = taskService.searchTasks(projId, query);
        System.out.println(AnsiColor.bold("\nSearch Results (" + results.size() + " found):"));
        for (Task t : results) {
            printTaskBadge(t);
        }
    }

    private void manageProjects() {
        System.out.println("\n1. Switch to existing project");
        System.out.println("2. Create new project");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            List<Project> projects = projectService.listAllProjects();
            if (projects.isEmpty()) {
                System.out.println(AnsiColor.yellow("No projects found."));
                return;
            }
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                System.out.printf("[%d] %s (%s)\n", i + 1, p.getName(), p.getId());
            }
            System.out.print("Select project number: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < projects.size()) {
                    activeProject = projects.get(idx);
                    System.out.println(AnsiColor.green("✓ Active project switched to: " + activeProject.getName()));
                } else {
                    System.out.println(AnsiColor.red("Invalid number."));
                }
            } catch (NumberFormatException e) {
                System.out.println(AnsiColor.red("Invalid input."));
            }
        } else if ("2".equals(choice)) {
            System.out.print("Project Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Description: ");
            String desc = scanner.nextLine().trim();

            User user = authService.getCurrentUser().orElseThrow();
            Project p = projectService.createProject(name, desc, user.getId());
            activeProject = p;
            System.out.println(AnsiColor.green("✓ Project '" + p.getName() + "' created and set as active."));
        }
    }

    private void exportTasks() {
        if (activeProject == null) {
            System.out.println(AnsiColor.red("No active project selected."));
            return;
        }

        System.out.println("1. Export to Markdown format");
        System.out.println("2. Export to CSV format");
        System.out.print("Format: ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            String md = exportService.exportTasksToMarkdown(activeProject.getId(), activeProject.getName());
            System.out.println("\n" + AnsiColor.cyan("--- MARKDOWN EXPORT ---"));
            System.out.println(md);
        } else if ("2".equals(choice)) {
            String csv = exportService.exportTasksToCsv(activeProject.getId());
            System.out.println("\n" + AnsiColor.cyan("--- CSV EXPORT ---"));
            System.out.println(csv);
        }
    }

    private void viewAuditLog() {
        List<AuditLog> logs = auditService.getRecentLogs(10);
        System.out.println(AnsiColor.bold("\n--- Recent System Audit Trail (Last 10 events) ---"));
        for (AuditLog l : logs) {
            System.out.printf("[%s] %s | By: %s | %s\n",
                    DateTimeUtil.format(l.getTimestamp()),
                    AnsiColor.yellow(l.getAction()),
                    l.getPerformedBy(),
                    l.getDetails());
        }
    }
}
