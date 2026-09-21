package com.flowforge.backend.workflow.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.flowforge.backend.workflow.entity.Workflow;
import com.flowforge.backend.workflow.entity.WorkflowStatus;

public class WorkflowResponse {

    private UUID id;
    private String name;
    private String description;
    private WorkflowStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkflowResponse() {
    }

    public WorkflowResponse(
            UUID id,
            String name,
            String description,
            WorkflowStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WorkflowResponse fromEntity(Workflow workflow) {

        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                workflow.getDescription(),
                workflow.getStatus(),
                workflow.getCreatedAt(),
                workflow.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
