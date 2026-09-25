package com.flowforge.backend.workflow.dto;

import java.util.UUID;

import com.flowforge.backend.workflow.entity.WorkflowEdge;

public class WorkflowEdgeResponse {

    private UUID id;
    private UUID sourceNodeId;
    private UUID targetNodeId;

    public WorkflowEdgeResponse() {
    }

    public WorkflowEdgeResponse(
            UUID id,
            UUID sourceNodeId,
            UUID targetNodeId) {

        this.id = id;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
    }

    public static WorkflowEdgeResponse fromEntity(
            WorkflowEdge edge) {

        return new WorkflowEdgeResponse(
                edge.getId(),
                edge.getSourceNode().getId(),
                edge.getTargetNode().getId()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceNodeId() {
        return sourceNodeId;
    }

    public UUID getTargetNodeId() {
        return targetNodeId;
    }
}