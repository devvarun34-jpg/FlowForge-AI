package com.flowforge.backend.ai.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowforge.backend.ai.dto.WorkflowApprovalResponse;
import com.flowforge.backend.ai.dto.WorkflowGenerationRequest;
import com.flowforge.backend.ai.dto.WorkflowGenerationResponse;
import com.flowforge.backend.ai.service.WorkflowApprovalService;
import com.flowforge.backend.ai.service.WorkflowGenerationService;
import com.flowforge.backend.workflow.service.WorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workflows/{workflowId}/ai")
public class AIWorkflowController {

    private final WorkflowGenerationService generationService;
    private final WorkflowApprovalService approvalService;
    private final WorkflowService workflowService;

    public AIWorkflowController(
            WorkflowGenerationService generationService,
            WorkflowApprovalService approvalService,
            WorkflowService workflowService) {

        this.generationService = generationService;
        this.approvalService = approvalService;
        this.workflowService = workflowService;
    }

    @PostMapping("/preview")
    public ResponseEntity<WorkflowGenerationResponse> previewWorkflow(
            @PathVariable UUID workflowId,
            @Valid @RequestBody WorkflowGenerationRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        workflowService.verifyWorkflowOwnership(
                workflowId,
                userEmail
        );

        WorkflowGenerationResponse response =
                generationService.generateWorkflow(
                        request.prompt()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<WorkflowApprovalResponse> approveWorkflow(
            @PathVariable UUID workflowId,
            @RequestBody WorkflowGenerationResponse generatedWorkflow,
            Authentication authentication) {

        String userEmail = authentication.getName();

        WorkflowApprovalResponse response =
                approvalService.approveWorkflow(
                        workflowId,
                        generatedWorkflow,
                        userEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}