package com.flowforge.backend.workflow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowforge.backend.workflow.entity.WorkflowNode;

public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, UUID> {

    List<WorkflowNode> findByWorkflowId(UUID workflowId);
}