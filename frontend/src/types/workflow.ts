
export type WorkflowStatus = 'ACTIVE' | 'INACTIVE' | 'DRAFT' | 'ARCHIVED' | string;

export interface Workflow {
  id: string;
  name: string;
  description: string | null;
  status: WorkflowStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateWorkflowRequest {
  name: string;
  description?: string;
}

export interface WorkflowNode {
  id: string;
  type: string;
  configuration: string;
  positionX: number;
  positionY: number;
}

export interface WorkflowEdge {
  id: string;
  sourceNodeId: string;
  targetNodeId: string;
}

export interface WorkflowGraph {
  nodes: WorkflowNode[];
  edges: WorkflowEdge[];
}
