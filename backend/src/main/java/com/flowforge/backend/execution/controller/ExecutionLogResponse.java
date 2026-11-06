package com.flowforge.backend.execution.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import com.flowforge.backend.execution.entity.ExecutionLog;
import com.flowforge.backend.execution.entity.ExecutionLogStatus;
import com.flowforge.backend.workflow.entity.NodeType;

public class ExecutionLogResponse {

    private UUID id;
    private UUID nodeId;
    private NodeType nodeType;
    private ExecutionLogStatus status;
    private String output;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public ExecutionLogResponse(
            UUID id,
            UUID nodeId,
            NodeType nodeType,
            ExecutionLogStatus status,
            String output,
            String errorMessage,
            LocalDateTime startedAt,
            LocalDateTime completedAt) {

        this.id = id;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.status = status;
        this.output = output;
        this.errorMessage = errorMessage;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static ExecutionLogResponse from(ExecutionLog log) {
        return new ExecutionLogResponse(
                log.getId(),
                log.getNodeId(),
                log.getNodeType(),
                log.getStatus(),
                log.getOutput(),
                log.getErrorMessage(),
                log.getStartedAt(),
                log.getCompletedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public ExecutionLogStatus getStatus() {
        return status;
    }

    public String getOutput() {
        return output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}