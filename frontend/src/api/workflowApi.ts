
import api from './axios';

import type {
  Workflow,
  CreateWorkflowRequest,
} from '../types/workflow';

export const getWorkflows = async (): Promise<Workflow[]> => {
  const response = await api.get<Workflow[]>('/api/workflows');
  return response.data;
};

export const createWorkflow = async (
  data: CreateWorkflowRequest
): Promise<Workflow> => {
  const response = await api.post<Workflow>('/api/workflows', data);
  return response.data;
};
