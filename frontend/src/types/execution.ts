
export type ExecutionStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'SUCCESS'
  | 'FAILED'
  | 'CANCELLED'
  | string;

export interface Execution {
  id: string;
  workflowId: string;
  status: ExecutionStatus;
  startedAt: string | null;
  completedAt: string | null;
  durationMs: number | null;
  errorMessage: string | null;
}

export interface ExecutionLog {
  id: string;
  nodeId: string;
  nodeType: string;
  status: string;
  output: string | null;
  errorMessage: string | null;
  startedAt: string | null;
  completedAt: string | null;
}
