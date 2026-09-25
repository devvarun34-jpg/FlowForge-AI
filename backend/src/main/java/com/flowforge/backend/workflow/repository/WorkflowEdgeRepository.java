package com.flowforge.backend.workflow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowforge.backend.workflow.entity.WorkflowEdge;

public interface WorkflowEdgeRepository extends JpaRepository<WorkflowEdge, UUID> {

    List<WorkflowEdge> findByWorkflowId(UUID workflowId);
}