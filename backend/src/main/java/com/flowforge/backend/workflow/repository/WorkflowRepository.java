package com.flowforge.backend.workflow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowforge.backend.workflow.entity.Workflow;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {

    List<Workflow> findByUserId(UUID userId);
}