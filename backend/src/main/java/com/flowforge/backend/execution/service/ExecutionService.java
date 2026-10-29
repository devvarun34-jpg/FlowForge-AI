
package com.flowforge.backend.execution.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowforge.backend.auth.entity.User;
import com.flowforge.backend.auth.repository.UserRepository;
import com.flowforge.backend.execution.action.DelayAction;
import com.flowforge.backend.execution.action.HttpAction;
import com.flowforge.backend.execution.action.LogAction;
import com.flowforge.backend.execution.entity.Execution;
import com.flowforge.backend.execution.entity.ExecutionStatus;
import com.flowforge.backend.execution.repository.ExecutionRepository;
import com.flowforge.backend.workflow.entity.NodeType;
import com.flowforge.backend.workflow.entity.Workflow;
import com.flowforge.backend.workflow.entity.WorkflowEdge;
import com.flowforge.backend.workflow.entity.WorkflowNode;
import com.flowforge.backend.workflow.repository.WorkflowEdgeRepository;
import com.flowforge.backend.workflow.repository.WorkflowNodeRepository;
import com.flowforge.backend.workflow.repository.WorkflowRepository;

@Service
public class ExecutionService {

    private final ExecutionRepository executionRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository workflowNodeRepository;
    private final WorkflowEdgeRepository workflowEdgeRepository;
    private final UserRepository userRepository;

    private final HttpAction httpAction;
    private final LogAction logAction;
    private final DelayAction delayAction;

    public ExecutionService(
            ExecutionRepository executionRepository,
            WorkflowRepository workflowRepository,
            WorkflowNodeRepository workflowNodeRepository,
            WorkflowEdgeRepository workflowEdgeRepository,
            UserRepository userRepository,
            HttpAction httpAction,
            LogAction logAction,
            DelayAction delayAction) {

        this.executionRepository = executionRepository;
        this.workflowRepository = workflowRepository;
        this.workflowNodeRepository = workflowNodeRepository;
        this.workflowEdgeRepository = workflowEdgeRepository;
        this.userRepository = userRepository;
        this.httpAction = httpAction;
        this.logAction = logAction;
        this.delayAction = delayAction;
    }

    /**
     * Starts and executes a user's workflow.
     */
    @Transactional
    public Execution startExecution(UUID workflowId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Workflow not found"));

        validateWorkflowOwnership(workflow, user);

        Execution execution = new Execution(workflow);
        execution.setStatus(ExecutionStatus.RUNNING);
        execution = executionRepository.saveAndFlush(execution);

        try {
            executeWorkflow(workflowId);

            execution.setStatus(ExecutionStatus.SUCCESS);
            execution.setCompletedAt(LocalDateTime.now());

        } catch (Exception exception) {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(
                    exception.getMessage() == null
                            ? "Workflow execution failed"
                            : exception.getMessage());
            execution.setCompletedAt(LocalDateTime.now());

            System.err.println(
                    "Workflow execution failed: "
                            + exception.getMessage());
        }

        return executionRepository.save(execution);
    }

    /**
     * Traverses and executes a linear workflow graph.
     */
    private void executeWorkflow(UUID workflowId) throws Exception {

        List<WorkflowNode> nodes =
                workflowNodeRepository.findByWorkflowId(workflowId);

        List<WorkflowEdge> edges =
                workflowEdgeRepository.findByWorkflowId(workflowId);

        if (nodes.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot execute a workflow without nodes");
        }

        Map<UUID, WorkflowNode> nodesById = new HashMap<>();

        for (WorkflowNode node : nodes) {
            nodesById.put(node.getId(), node);
        }

        Set<UUID> nodesWithIncomingEdges = new HashSet<>();

        Map<UUID, List<WorkflowEdge>> outgoingEdges = new HashMap<>();

        for (WorkflowEdge edge : edges) {
            UUID sourceId = edge.getSourceNode().getId();
            UUID targetId = edge.getTargetNode().getId();

            if (!nodesById.containsKey(sourceId)
                    || !nodesById.containsKey(targetId)) {
                throw new IllegalStateException(
                        "Workflow edge references a node outside the workflow");
            }

            nodesWithIncomingEdges.add(targetId);

            outgoingEdges
                    .computeIfAbsent(
                            sourceId, ignored -> new ArrayList<>())
                    .add(edge);
        }

        List<WorkflowNode> startNodes = nodes.stream()
                .filter(node ->
                        !nodesWithIncomingEdges.contains(node.getId()))
                .toList();

        if (startNodes.size() != 1) {
            throw new IllegalStateException(
                    "Workflow must have exactly one starting node");
        }

        WorkflowNode currentNode = startNodes.get(0);
        Set<UUID> visitedNodes = new HashSet<>();

        while (currentNode != null) {

            UUID currentNodeId = currentNode.getId();

            if (!visitedNodes.add(currentNodeId)) {
                throw new IllegalStateException(
                        "Cycle detected in workflow graph");
            }

            System.out.println(
                    "Executing node: " + currentNodeId
                            + " (" + currentNode.getType() + ")");

            String result = executeNode(currentNode);

            System.out.println("Node result: " + result);

            List<WorkflowEdge> nextEdges =
                    outgoingEdges.getOrDefault(
                            currentNodeId, List.of());

            if (nextEdges.size() > 1) {
                throw new IllegalStateException(
                        "Branching workflows are not supported yet");
            }

            if (nextEdges.isEmpty()) {
                currentNode = null;
            } else {
                UUID nextNodeId =
                        nextEdges.get(0).getTargetNode().getId();

                currentNode = nodesById.get(nextNodeId);
            }
        }

        if (visitedNodes.size() != nodes.size()) {
            throw new IllegalStateException(
                    "Workflow contains disconnected nodes or a separate cycle");
        }
    }

    /**
     * Selects the action corresponding to a node's type.
     */
    private String executeNode(WorkflowNode node) throws Exception {

        String configuration = node.getConfiguration();

        NodeType type = node.getType();

        if (type == null) {
            throw new IllegalArgumentException(
                    "Workflow node has no type");
        }

        return switch (type) {
            case HTTP -> httpAction.execute(configuration);
            case LOG -> logAction.execute(configuration);
            case DELAY -> delayAction.execute(configuration);
        };
    }

    @Transactional
    public Execution completeExecution(UUID executionId) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Execution not found"));

        execution.setStatus(ExecutionStatus.SUCCESS);
        execution.setCompletedAt(LocalDateTime.now());

        return executionRepository.save(execution);
    }

    @Transactional
    public Execution failExecution(
            UUID executionId,
            String errorMessage) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Execution not found"));

        execution.setStatus(ExecutionStatus.FAILED);
        execution.setErrorMessage(errorMessage);
        execution.setCompletedAt(LocalDateTime.now());

        return executionRepository.save(execution);
    }

    @Transactional(readOnly = true)
    public List<Execution> getWorkflowExecutions(
            UUID workflowId,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Workflow not found"));

        validateWorkflowOwnership(workflow, user);

        return executionRepository.findByWorkflowId(workflowId);
    }

    private void validateWorkflowOwnership(
            Workflow workflow,
            User user) {

        if (!workflow.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You do not have permission to access this workflow");
        }
    }
}
