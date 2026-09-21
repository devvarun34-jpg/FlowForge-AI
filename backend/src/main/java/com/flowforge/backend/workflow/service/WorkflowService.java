package com.flowforge.backend.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowforge.backend.auth.entity.User;
import com.flowforge.backend.auth.repository.UserRepository;
import com.flowforge.backend.workflow.dto.CreateWorkflowRequest;
import com.flowforge.backend.workflow.dto.WorkflowResponse;
import com.flowforge.backend.workflow.entity.Workflow;
import com.flowforge.backend.workflow.repository.WorkflowRepository;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;

    public WorkflowService(
            WorkflowRepository workflowRepository,
            UserRepository userRepository) {

        this.workflowRepository = workflowRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkflowResponse createWorkflow(
            CreateWorkflowRequest request,
            String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Workflow workflow = new Workflow(
                request.getName(),
                request.getDescription(),
                user
        );

        Workflow savedWorkflow = workflowRepository.save(workflow);

        return WorkflowResponse.fromEntity(savedWorkflow);
    }

    @Transactional(readOnly = true)
    public List<WorkflowResponse> getUserWorkflows(
            String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return workflowRepository
                .findByUserId(user.getId())
                .stream()
                .map(WorkflowResponse::fromEntity)
                .toList();
    }
}