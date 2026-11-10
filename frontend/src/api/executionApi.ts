
import api from './axios';

import type {
  Execution,
  ExecutionLog,
} from '../types/execution';

export const getWorkflowExecutions = async (
  workflowId: string
): Promise<Execution[]> => {
  const response = await api.get<Execution[]>(
    `/api/workflows/${workflowId}/executions`
  );

  return response.data;
};

export const getExecutionLogs = async (
  workflowId: string,
  executionId: string
): Promise<ExecutionLog[]> => {
  const response = await api.get<ExecutionLog[]>(
    `/api/workflows/${workflowId}/executions/${executionId}/logs`
  );

  return response.data;
};
