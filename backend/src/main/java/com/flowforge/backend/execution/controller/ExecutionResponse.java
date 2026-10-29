
package com.flowforge.backend.execution.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import com.flowforge.backend.execution.entity.Execution;
import com.flowforge.backend.execution.entity.ExecutionStatus;

public class ExecutionResponse {

    private UUID id;
    private UUID workflowId;
    private ExecutionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;

    public ExecutionResponse() {
    }

    public ExecutionResponse(
            UUID id,
            UUID workflowId,
            ExecutionStatus status,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            String errorMessage) {
        this.id = id;
        this.workflowId = workflowId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
    }

    public static ExecutionResponse from(Execution execution) {
        return new ExecutionResponse(
                execution.getId(),
                execution.getWorkflow().getId(),
                execution.getStatus(),
                execution.getStartedAt(),
                execution.getCompletedAt(),
                execution.getErrorMessage()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkflowId() {
        return workflowId;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
