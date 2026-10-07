package com.taskforge.service;

import com.taskforge.model.Project;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.util.Validator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing project creation, retrieval, and membership.
 */
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuditService auditService;

    public ProjectService(ProjectRepository projectRepository, AuditService auditService) {
        this.projectRepository = projectRepository;
        this.auditService = auditService;
    }

    public Project createProject(String name, String description, String ownerId) {
        Validator.requireNotEmpty(name, "Project name");
        Validator.requireNotEmpty(ownerId, "Owner ID");

        if (projectRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Project with name '" + name + "' already exists.");
        }

        String id = "prj-" + UUID.randomUUID().toString().substring(0, 6);
        Project project = new Project(id, name.trim(), description, ownerId);
        projectRepository.save(project);

        auditService.log("PROJECT_CREATED", ownerId, "Created project " + project.getName() + " (" + id + ")");
        return project;
    }

    public Optional<Project> getProject(String id) {
        return projectRepository.findById(id);
    }

    public List<Project> listProjectsForUser(String userId) {
        return projectRepository.findByMemberId(userId);
    }

    public List<Project> listAllProjects() {
        return projectRepository.findAll();
    }

    public boolean addMember(String projectId, String userId, String requesterId) {
        Optional<Project> opt = projectRepository.findById(projectId);
        if (opt.isEmpty()) return false;

        Project project = opt.get();
        if (!project.getOwnerId().equals(requesterId)) {
            throw new SecurityException("Only the project owner can add team members.");
        }

        project.addMember(userId);
        projectRepository.save(project);
        auditService.log("PROJECT_MEMBER_ADDED", requesterId, "Added " + userId + " to " + project.getName());
        return true;
    }

    public boolean deleteProject(String projectId, String requesterId) {
        Optional<Project> opt = projectRepository.findById(projectId);
        if (opt.isEmpty()) return false;

        Project project = opt.get();
        if (!project.getOwnerId().equals(requesterId)) {
            throw new SecurityException("Only the project owner can delete this project.");
        }

        boolean deleted = projectRepository.deleteById(projectId);
        if (deleted) {
            auditService.log("PROJECT_DELETED", requesterId, "Deleted project " + project.getName());
        }
        return deleted;
    }
}
