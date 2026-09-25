package com.flowforge.backend.workflow.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowforge.backend.auth.entity.User;
import com.flowforge.backend.auth.repository.UserRepository;
import com.flowforge.backend.workflow.dto.CreateWorkflowNodeRequest;
import com.flowforge.backend.workflow.dto.WorkflowNodeResponse;
import com.flowforge.backend.workflow.entity.Workflow;
import com.flowforge.backend.workflow.entity.WorkflowNode;
import com.flowforge.backend.workflow.repository.WorkflowNodeRepository;
import com.flowforge.backend.workflow.repository.WorkflowRepository;

@Service
public class WorkflowNodeService {

    private final WorkflowNodeRepository workflowNodeRepository;
    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;

    public WorkflowNodeService(
            WorkflowNodeRepository workflowNodeRepository,
            WorkflowRepository workflowRepository,
            UserRepository userRepository) {

        this.workflowNodeRepository = workflowNodeRepository;
        this.workflowRepository = workflowRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkflowNodeResponse createNode(
            UUID workflowId,
            CreateWorkflowNodeRequest request,
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

        WorkflowNode node = new WorkflowNode(
                workflow,
                request.getType(),
                request.getConfiguration(),
                request.getPositionX(),
                request.getPositionY()
        );

        WorkflowNode savedNode =
                workflowNodeRepository.save(node);

        return WorkflowNodeResponse.fromEntity(savedNode);
    }

    @Transactional(readOnly = true)
    public List<WorkflowNodeResponse> getWorkflowNodes(
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

        return workflowNodeRepository
                .findByWorkflowId(workflowId)
                .stream()
                .map(WorkflowNodeResponse::fromEntity)
                .toList();
    }
}