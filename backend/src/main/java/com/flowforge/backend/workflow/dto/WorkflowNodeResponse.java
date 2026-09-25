package com.flowforge.backend.workflow.dto;

import java.util.UUID;

import com.flowforge.backend.workflow.entity.NodeType;
import com.flowforge.backend.workflow.entity.WorkflowNode;

public class WorkflowNodeResponse {

    private UUID id;
    private NodeType type;
    private String configuration;
    private Double positionX;
    private Double positionY;

    public WorkflowNodeResponse() {
    }

    public WorkflowNodeResponse(
            UUID id,
            NodeType type,
            String configuration,
            Double positionX,
            Double positionY) {

        this.id = id;
        this.type = type;
        this.configuration = configuration;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public static WorkflowNodeResponse fromEntity(
            WorkflowNode node) {

        return new WorkflowNodeResponse(
                node.getId(),
                node.getType(),
                node.getConfiguration(),
                node.getPositionX(),
                node.getPositionY()
        );
    }

    public UUID getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public String getConfiguration() {
        return configuration;
    }

    public Double getPositionX() {
        return positionX;
    }

    public Double getPositionY() {
        return positionY;
    }
}