package com.flowforge.backend.ai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowforge.backend.ai.dto.GeneratedEdge;
import com.flowforge.backend.ai.dto.GeneratedNode;
import com.flowforge.backend.ai.dto.WorkflowApprovalResponse;
import com.flowforge.backend.ai.dto.WorkflowGenerationResponse;
import com.flowforge.backend.ai.validation.GeneratedWorkflowValidator;
import com.flowforge.backend.workflow.dto.CreateWorkflowNodeRequest;
import com.flowforge.backend.workflow.dto.WorkflowEdgeResponse;
import com.flowforge.backend.workflow.dto.WorkflowNodeResponse;
import com.flowforge.backend.workflow.service.WorkflowEdgeService;
import com.flowforge.backend.workflow.service.WorkflowNodeService;
import com.flowforge.backend.workflow.service.WorkflowService;

@Service
public class WorkflowApprovalService {

    private final WorkflowService workflowService;
    private final WorkflowNodeService workflowNodeService;
    private final WorkflowEdgeService workflowEdgeService;
    private final GeneratedWorkflowValidator validator;

    public WorkflowApprovalService(
            WorkflowService workflowService,
            WorkflowNodeService workflowNodeService,
            WorkflowEdgeService workflowEdgeService,
            GeneratedWorkflowValidator validator) {

        this.workflowService = workflowService;
        this.workflowNodeService = workflowNodeService;
        this.workflowEdgeService = workflowEdgeService;
        this.validator = validator;
    }

    @Transactional
    public WorkflowApprovalResponse approveWorkflow(
            UUID workflowId,
            WorkflowGenerationResponse generatedWorkflow,
            String userEmail) {

        // Verify workflow ownership.
        workflowService.verifyWorkflowOwnership(
                workflowId,
                userEmail
        );

        // Validate the submitted preview again on the server.
        validator.validate(generatedWorkflow);

        // Prevent accidental mixing with an existing workflow.
        if (!workflowNodeService
                .getWorkflowNodes(workflowId, userEmail).isEmpty()
                || !workflowEdgeService
                .getWorkflowEdges(workflowId, userEmail).isEmpty()) {

            throw new IllegalStateException(
                    "AI-generated workflows can only be approved "
                    + "into an empty workflow."
            );
        }

        List<WorkflowNodeResponse> savedNodes = new ArrayList<>();
        List<WorkflowEdgeResponse> savedEdges = new ArrayList<>();

        // Maps generated node indexes to persisted database IDs.
        Map<Integer, UUID> nodeIds = new HashMap<>();

        // Save all nodes first.
        List<GeneratedNode> generatedNodes =
                generatedWorkflow.nodes();

        for (int i = 0; i < generatedNodes.size(); i++) {

            GeneratedNode generatedNode = generatedNodes.get(i);

            CreateWorkflowNodeRequest request =
                    new CreateWorkflowNodeRequest();

            request.setType(generatedNode.type());
            request.setConfiguration(
                    generatedNode.configuration().toString()
            );
            request.setPositionX(generatedNode.positionX());
            request.setPositionY(generatedNode.positionY());

            WorkflowNodeResponse savedNode =
                    workflowNodeService.createNode(
                            workflowId,
                            request,
                            userEmail
                    );

            savedNodes.add(savedNode);
            nodeIds.put(i, savedNode.getId());
        }

        // Save edges after all nodes have been persisted.
        for (GeneratedEdge generatedEdge :
                generatedWorkflow.edges()) {

            UUID sourceNodeId =
                    nodeIds.get(generatedEdge.sourceNodeIndex());

            UUID targetNodeId =
                    nodeIds.get(generatedEdge.targetNodeIndex());

            WorkflowEdgeResponse savedEdge =
                    workflowEdgeService.createEdge(
                            workflowId,
                            sourceNodeId,
                            targetNodeId,
                            userEmail
                    );

            savedEdges.add(savedEdge);
        }

        return new WorkflowApprovalResponse(
                workflowId,
                generatedWorkflow.summary(),
                savedNodes,
                savedEdges
        );
    }
}