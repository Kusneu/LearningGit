package com.taskforge.repository;

import com.taskforge.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository for Project entities.
 */
public class ProjectRepository extends InMemoryRepository<Project, String> {

    public ProjectRepository() {
        super(Project::getId);
    }

    public List<Project> findByOwnerId(String ownerId) {
        if (ownerId == null) return List.of();
        return storage.values().stream()
                .filter(p -> p.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Project> findByMemberId(String memberId) {
        if (memberId == null) return List.of();
        return storage.values().stream()
                .filter(p -> p.getMemberIds().contains(memberId))
                .collect(Collectors.toList());
    }

    public Optional<Project> findByName(String name) {
        if (name == null) return Optional.empty();
        return storage.values().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }
}
