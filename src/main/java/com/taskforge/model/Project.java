package com.taskforge.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a software project grouping multiple tasks.
 */
public class Project {
    private final String id;
    private String name;
    private String description;
    private final String ownerId;
    private final Set<String> memberIds;
    private final LocalDateTime createdAt;

    public Project(String id, String name, String description, String ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.memberIds = new HashSet<>();
        this.memberIds.add(ownerId);
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Set<String> getMemberIds() {
        return Collections.unmodifiableSet(memberIds);
    }

    public void addMember(String userId) {
        if (userId != null) {
            this.memberIds.add(userId);
        }
    }

    public void removeMember(String userId) {
        if (userId != null && !userId.equals(ownerId)) {
            this.memberIds.remove(userId);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Project[id=%s, name='%s', owner=%s, members=%d]",
                id, name, ownerId, memberIds.size());
    }
}
