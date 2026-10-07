package com.taskforge.service;

import com.taskforge.model.Task;
import com.taskforge.repository.TaskRepository;
import com.taskforge.util.DateTimeUtil;

import java.util.List;

/**
 * Generates formatted text exports (Markdown and CSV) of project tasks.
 */
public class ExportService {
    private final TaskRepository taskRepository;

    public ExportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public String exportTasksToMarkdown(String projectId, String projectName) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        StringBuilder sb = new StringBuilder();
        sb.append("# TaskForge Export: ").append(projectName).append("\n\n");
        sb.append("| ID | Title | Priority | Status | Assignee | Created |\n");
        sb.append("|---|---|---|---|---|---|\n");

        for (Task t : tasks) {
            sb.append(String.format("| %s | %s | %s | %s | %s | %s |\n",
                    t.getId(),
                    t.getTitle().replace("|", "\\|"),
                    t.getPriority(),
                    t.getStatus().getDisplayName(),
                    t.getAssigneeId() != null ? t.getAssigneeId() : "Unassigned",
                    DateTimeUtil.formatDateOnly(t.getCreatedAt())));
        }
        return sb.toString();
    }

    public String exportTasksToCsv(String projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        StringBuilder sb = new StringBuilder();
        sb.append("id,title,priority,status,assignee,created_at\n");

        for (Task t : tasks) {
            sb.append(escapeCsv(t.getId())).append(",");
            sb.append(escapeCsv(t.getTitle())).append(",");
            sb.append(t.getPriority()).append(",");
            sb.append(escapeCsv(t.getStatus().name())).append(",");
            sb.append(escapeCsv(t.getAssigneeId() != null ? t.getAssigneeId() : "")).append(",");
            sb.append(DateTimeUtil.formatDateOnly(t.getCreatedAt())).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
