package com.flowforge.backend.execution.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowforge.backend.execution.entity.ExecutionLog;

public interface ExecutionLogRepository
        extends JpaRepository<ExecutionLog, UUID> {

    List<ExecutionLog> findByExecutionIdOrderByStartedAtAsc(
            UUID executionId);
}