package com.flowforge.backend.workflow.dto;

import java.util.List;

public class WorkflowGraphResponse {

    private List<WorkflowNodeResponse> nodes;
    private List<WorkflowEdgeResponse> edges;

    public WorkflowGraphResponse() {
    }

    public WorkflowGraphResponse(
            List<WorkflowNodeResponse> nodes,
            List<WorkflowEdgeResponse> edges) {

        this.nodes = nodes;
        this.edges = edges;
    }

    public List<WorkflowNodeResponse> getNodes() {
        return nodes;
    }

    public List<WorkflowEdgeResponse> getEdges() {
        return edges;
    }
}