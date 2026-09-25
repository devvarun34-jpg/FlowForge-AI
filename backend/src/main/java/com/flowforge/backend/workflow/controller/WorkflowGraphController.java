package com.flowforge.backend.workflow.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flowforge.backend.workflow.dto.CreateWorkflowNodeRequest;
import com.flowforge.backend.workflow.dto.WorkflowEdgeResponse;
import com.flowforge.backend.workflow.dto.WorkflowNodeResponse;
import com.flowforge.backend.workflow.service.WorkflowEdgeService;
import com.flowforge.backend.workflow.service.WorkflowNodeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workflows/{workflowId}")
public class WorkflowGraphController {

    private final WorkflowNodeService workflowNodeService;
    private final WorkflowEdgeService workflowEdgeService;

    public WorkflowGraphController(
            WorkflowNodeService workflowNodeService,
            WorkflowEdgeService workflowEdgeService) {

        this.workflowNodeService = workflowNodeService;
        this.workflowEdgeService = workflowEdgeService;
    }

    @PostMapping("/nodes")
    public ResponseEntity<WorkflowNodeResponse> createNode(
            @PathVariable UUID workflowId,
            @Valid @RequestBody CreateWorkflowNodeRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        WorkflowNodeResponse response =
                workflowNodeService.createNode(
                        workflowId,
                        request,
                        userEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/nodes")
    public ResponseEntity<List<WorkflowNodeResponse>> getNodes(
            @PathVariable UUID workflowId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        List<WorkflowNodeResponse> nodes =
                workflowNodeService.getWorkflowNodes(
                        workflowId,
                        userEmail
                );

        return ResponseEntity.ok(nodes);
    }

    @PostMapping("/edges")
    public ResponseEntity<WorkflowEdgeResponse> createEdge(
            @PathVariable UUID workflowId,
            @RequestParam UUID sourceNodeId,
            @RequestParam UUID targetNodeId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        WorkflowEdgeResponse response =
                workflowEdgeService.createEdge(
                        workflowId,
                        sourceNodeId,
                        targetNodeId,
                        userEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/edges")
    public ResponseEntity<List<WorkflowEdgeResponse>> getEdges(
            @PathVariable UUID workflowId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        List<WorkflowEdgeResponse> edges =
                workflowEdgeService.getWorkflowEdges(
                        workflowId,
                        userEmail
                );

        return ResponseEntity.ok(edges);
    }
}