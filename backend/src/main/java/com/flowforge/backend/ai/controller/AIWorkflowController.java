
package com.flowforge.backend.ai.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowforge.backend.ai.dto.WorkflowGenerationRequest;
import com.flowforge.backend.ai.dto.WorkflowGenerationResponse;
import com.flowforge.backend.ai.service.WorkflowGenerationService;
import com.flowforge.backend.workflow.service.WorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workflows/{workflowId}/ai")
public class AIWorkflowController {

    private final WorkflowGenerationService generationService;
    private final WorkflowService workflowService;

    public AIWorkflowController(
            WorkflowGenerationService generationService,
            WorkflowService workflowService) {

        this.generationService = generationService;
        this.workflowService = workflowService;
    }

    @PostMapping("/preview")
    public ResponseEntity<WorkflowGenerationResponse> previewWorkflow(
            @PathVariable UUID workflowId,
            @Valid @RequestBody WorkflowGenerationRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        // Verify that the workflow belongs to the authenticated user.
        workflowService.verifyWorkflowOwnership(
                workflowId,
                userEmail
        );

        // Generate and validate the workflow without saving it.
        WorkflowGenerationResponse response =
                generationService.generateWorkflow(
                        request.prompt()
                );

        return ResponseEntity.ok(response);
    }
}
