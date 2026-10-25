
package com.flowforge.backend.execution.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowforge.backend.execution.entity.Execution;

public interface ExecutionRepository
        extends JpaRepository<Execution, UUID> {

    List<Execution> findByWorkflowId(UUID workflowId);
}
