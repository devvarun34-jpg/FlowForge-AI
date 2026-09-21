package com.flowforge.backend.workflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowforge.backend.workflow.dto.CreateWorkflowRequest;
import com.flowforge.backend.workflow.dto.WorkflowResponse;
import com.flowforge.backend.workflow.service.WorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        WorkflowResponse response =
                workflowService.createWorkflow(
                        request,
                        userEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> getMyWorkflows(
            Authentication authentication) {

        String userEmail = authentication.getName();

        List<WorkflowResponse> workflows =
                workflowService.getUserWorkflows(userEmail);

        return ResponseEntity.ok(workflows);
    }
}