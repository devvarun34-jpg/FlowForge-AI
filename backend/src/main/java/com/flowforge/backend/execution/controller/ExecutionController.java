
package com.flowforge.backend.execution.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowforge.backend.execution.entity.Execution;
import com.flowforge.backend.execution.service.ExecutionService;

@RestController
@RequestMapping("/api/workflows/{workflowId}/executions")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping
    public ResponseEntity<ExecutionResponse> startExecution(
            @PathVariable("workflowId") UUID workflowId,
            Authentication authentication) {
            System.out.println("START EXECUTION CONTROLLER REACHED");
        Execution execution = executionService.startExecution(
                workflowId,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ExecutionResponse.from(execution));
    }

    @GetMapping
    public ResponseEntity<List<ExecutionResponse>> getExecutions(
            @PathVariable("workflowId") UUID workflowId,
            Authentication authentication) {

        List<Execution> executions = executionService.getWorkflowExecutions(
                workflowId,
                authentication.getName()
        );

        List<ExecutionResponse> responses = executions.stream()
                .map(ExecutionResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
