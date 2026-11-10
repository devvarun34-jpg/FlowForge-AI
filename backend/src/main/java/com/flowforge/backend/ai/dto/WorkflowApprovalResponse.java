package com.flowforge.backend.ai.dto;

import java.util.List;
import java.util.UUID;

import com.flowforge.backend.workflow.dto.WorkflowEdgeResponse;
import com.flowforge.backend.workflow.dto.WorkflowNodeResponse;

public record WorkflowApprovalResponse(
        UUID workflowId,
        String summary,
        List<WorkflowNodeResponse> nodes,
        List<WorkflowEdgeResponse> edges
) {
}