package com.flowforge.backend.workflow.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowforge.backend.auth.entity.User;
import com.flowforge.backend.auth.repository.UserRepository;
import com.flowforge.backend.workflow.dto.WorkflowEdgeResponse;
import com.flowforge.backend.workflow.entity.Workflow;
import com.flowforge.backend.workflow.entity.WorkflowEdge;
import com.flowforge.backend.workflow.entity.WorkflowNode;
import com.flowforge.backend.workflow.repository.WorkflowEdgeRepository;
import com.flowforge.backend.workflow.repository.WorkflowNodeRepository;
import com.flowforge.backend.workflow.repository.WorkflowRepository;

@Service
public class WorkflowEdgeService {

    private final WorkflowEdgeRepository workflowEdgeRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository workflowNodeRepository;
    private final UserRepository userRepository;

    public WorkflowEdgeService(
            WorkflowEdgeRepository workflowEdgeRepository,
            WorkflowRepository workflowRepository,
            WorkflowNodeRepository workflowNodeRepository,
            UserRepository userRepository) {

        this.workflowEdgeRepository = workflowEdgeRepository;
        this.workflowRepository = workflowRepository;
        this.workflowNodeRepository = workflowNodeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkflowEdgeResponse createEdge(
            UUID workflowId,
            UUID sourceNodeId,
            UUID targetNodeId,
            String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Workflow workflow = workflowRepository
                .findById(workflowId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Workflow not found"));

        if (!workflow.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You do not have access to this workflow");
        }

        WorkflowNode sourceNode = workflowNodeRepository
                .findById(sourceNodeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Source node not found"));

        WorkflowNode targetNode = workflowNodeRepository
                .findById(targetNodeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Target node not found"));

        if (!sourceNode.getWorkflow().getId().equals(workflowId)
                || !targetNode.getWorkflow().getId().equals(workflowId)) {

            throw new IllegalArgumentException(
                    "Both nodes must belong to this workflow");
        }

        if (sourceNodeId.equals(targetNodeId)) {
            throw new IllegalArgumentException(
                    "A node cannot connect to itself");
        }

        WorkflowEdge edge = new WorkflowEdge(
                workflow,
                sourceNode,
                targetNode
        );

        WorkflowEdge savedEdge =
                workflowEdgeRepository.save(edge);

        return WorkflowEdgeResponse.fromEntity(savedEdge);
    }

    @Transactional(readOnly = true)
    public List<WorkflowEdgeResponse> getWorkflowEdges(
            UUID workflowId,
            String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Workflow workflow = workflowRepository
                .findById(workflowId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Workflow not found"));

        if (!workflow.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You do not have access to this workflow");
        }

        return workflowEdgeRepository
                .findByWorkflowId(workflowId)
                .stream()
                .map(WorkflowEdgeResponse::fromEntity)
                .toList();
    }
}