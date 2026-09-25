package com.flowforge.backend.workflow.dto;

import com.flowforge.backend.workflow.entity.NodeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateWorkflowNodeRequest {

    @NotNull(message = "Node type is required")
    private NodeType type;

    @NotBlank(message = "Node configuration is required")
    private String configuration;

    @NotNull(message = "Position X is required")
    private Double positionX;

    @NotNull(message = "Position Y is required")
    private Double positionY;

    public CreateWorkflowNodeRequest() {
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }
}