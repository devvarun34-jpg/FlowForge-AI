
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import {
  Alert,
  Avatar,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  Paper,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material';

import {
  AccountTreeRounded,
  AccessTimeRounded,
  AddRounded,
  AutoAwesomeRounded,
  CheckCircleOutlineRounded,
  ErrorOutlineRounded,
  LogoutRounded,
  RefreshRounded,
  WorkspacesRounded,
} from '@mui/icons-material';

import { useAuthStore } from '../store/authStore';
import { getWorkflows, createWorkflow } from '../api/workflowApi';
import { getWorkflowExecutions } from '../api/executionApi';

import type { Workflow } from '../types/workflow';
import type { Execution } from '../types/execution';

const DashboardPage = () => {
  const navigate = useNavigate();

  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [executions, setExecutions] = useState<Execution[]>([]);

  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const [createOpen, setCreateOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [workflowName, setWorkflowName] = useState('');
  const [workflowDescription, setWorkflowDescription] = useState('');
  const [createError, setCreateError] = useState('');

  const loadDashboard = useCallback(async (isRefresh = false) => {
    if (isRefresh) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError('');

    try {
      const workflowData = await getWorkflows();
      setWorkflows(workflowData);

      const executionResults = await Promise.allSettled(
        workflowData.map((workflow) =>
          getWorkflowExecutions(workflow.id)
        )
      );

      const allExecutions = executionResults.flatMap((result) =>
        result.status === 'fulfilled' ? result.value : []
      );

      setExecutions(allExecutions);
    } catch {
      setError(
        'Unable to load your workflows. Check your connection and try again.'
      );
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    void loadDashboard();
  }, [loadDashboard]);

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  const handleCreateWorkflow = async () => {
    if (!workflowName.trim()) {
      setCreateError('Please enter a workflow name.');
      return;
    }

    setCreating(true);
    setCreateError('');

    try {
      await createWorkflow({
        name: workflowName.trim(),
        description: workflowDescription.trim(),
      });

      setWorkflowName('');
      setWorkflowDescription('');
      setCreateOpen(false);

      await loadDashboard(true);
    } catch {
      setCreateError(
        'Could not create the workflow. Please try again.'
      );
    } finally {
      setCreating(false);
    }
  };

  const successfulExecutions = useMemo(
    () =>
      executions.filter(
        (execution) => execution.status === 'SUCCESS'
      ).length,
    [executions]
  );

  const failedExecutions = useMemo(
    () =>
      executions.filter(
        (execution) => execution.status === 'FAILED'
      ).length,
    [executions]
  );

  const recentExecutions = useMemo(
    () =>
      [...executions]
        .sort((a, b) => {
          const dateA = a.startedAt
            ? new Date(a.startedAt).getTime()
            : 0;

          const dateB = b.startedAt
            ? new Date(b.startedAt).getTime()
            : 0;

          return dateB - dateA;
        })
        .slice(0, 6),
    [executions]
  );

  const workflowNameById = useMemo(
    () =>
      new Map(
        workflows.map((workflow) => [
          workflow.id,
          workflow.name,
        ])
      ),
    [workflows]
  );

  const formatDate = (value: string | null) => {
    if (!value) return 'Not available';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return 'Not available';
    }

    return date.toLocaleString([], {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  };

  const getStatusColor = (
    status: string
  ): 'success' | 'error' | 'warning' | 'default' => {
    switch (status) {
      case 'SUCCESS':
      case 'ACTIVE':
        return 'success';

      case 'FAILED':
        return 'error';

      case 'RUNNING':
      case 'PENDING':
        return 'warning';

      default:
        return 'default';
    }
  };

  const panelSx = {
    backgroundColor: 'background.paper',
    border: '1px solid',
    borderColor: 'divider',
    borderRadius: 3,
  };

  const statCards = [
    {
      label: 'Total workflows',
      value: workflows.length,
      icon: <WorkspacesRounded />,
      detail: 'Your saved workflows',
    },
    {
      label: 'Total executions',
      value: executions.length,
      icon: <AccountTreeRounded />,
      detail: 'Recorded workflow runs',
    },
    {
      label: 'Successful runs',
      value: successfulExecutions,
      icon: <CheckCircleOutlineRounded />,
      detail: 'Completed successfully',
    },
    {
      label: 'Failed runs',
      value: failedExecutions,
      icon: <ErrorOutlineRounded />,
      detail: 'Runs requiring attention',
    },
  ];

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        backgroundColor: 'background.default',
        color: 'text.primary',
      }}
    >
      {/* Sidebar */}
      <Box
        component="aside"
        sx={{
          width: { xs: 68, md: 250 },
          flexShrink: 0,
          borderRight: '1px solid',
          borderColor: 'divider',
          backgroundColor: 'background.paper',
          display: 'flex',
          flexDirection: 'column',
          p: { xs: 1, md: 2 },
        }}
      >
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: { xs: 'center', md: 'flex-start' },
            gap: 1.5,
            px: { xs: 0, md: 1 },
            py: 2,
          }}
        >
          <AccountTreeRounded
            sx={{ color: 'primary.main', fontSize: 30 }}
          />

          <Typography
            sx={{
              display: { xs: 'none', md: 'block' },
              fontWeight: 900,
              letterSpacing: '-0.7px',
            }}
          >
            FLOWFORGE
            <Box component="span" sx={{ color: 'primary.main' }}>
              .
            </Box>
          </Typography>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Typography
          sx={{
            display: { xs: 'none', md: 'block' },
            px: 1,
            mb: 1,
            color: 'text.secondary',
            fontSize: 10,
            fontWeight: 800,
            letterSpacing: 1.2,
          }}
        >
          WORKSPACE
        </Typography>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
          <Button
            fullWidth
            startIcon={<WorkspacesRounded />}
            sx={{
              justifyContent: { xs: 'center', md: 'flex-start' },
              minWidth: 0,
              px: { xs: 1, md: 2 },
              color: 'primary.main',
              backgroundColor: 'action.selected',
              '& .MuiButton-startIcon': {
                mr: { xs: 0, md: 1 },
              },
            }}
          >
            <Box
              component="span"
              sx={{ display: { xs: 'none', md: 'inline' } }}
            >
              Overview
            </Box>
          </Button>

          <Tooltip title="Workflow management coming next" placement="right">
            <span>
              <Button
                fullWidth
                disabled
                startIcon={<AccountTreeRounded />}
                sx={{
                  justifyContent: { xs: 'center', md: 'flex-start' },
                  minWidth: 0,
                  px: { xs: 1, md: 2 },
                  '& .MuiButton-startIcon': {
                    mr: { xs: 0, md: 1 },
                  },
                }}
              >
                <Box
                  component="span"
                  sx={{ display: { xs: 'none', md: 'inline' } }}
                >
                  My Workflows
                </Box>
              </Button>
            </span>
          </Tooltip>

          <Tooltip title="AI Studio coming next" placement="right">
            <span>
              <Button
                fullWidth
                disabled
                startIcon={<AutoAwesomeRounded />}
                sx={{
                  justifyContent: { xs: 'center', md: 'flex-start' },
                  minWidth: 0,
                  px: { xs: 1, md: 2 },
                  '& .MuiButton-startIcon': {
                    mr: { xs: 0, md: 1 },
                  },
                }}
              >
                <Box
                  component="span"
                  sx={{ display: { xs: 'none', md: 'inline' } }}
                >
                  AI Studio
                </Box>
              </Button>
            </span>
          </Tooltip>
        </Box>

        <Box sx={{ flexGrow: 1 }} />

        <Divider sx={{ my: 2 }} />

        <Box
          sx={{
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            alignItems: 'center',
            gap: 1.5,
            p: 1,
          }}
        >
          <Avatar
            sx={{
              width: 36,
              height: 36,
              backgroundColor: 'primary.main',
              color: '#071316',
              fontWeight: 800,
            }}
          >
            {user?.name?.charAt(0)?.toUpperCase() || 'D'}
          </Avatar>

          <Box
            sx={{
              minWidth: 0,
              flexGrow: 1,
              display: { xs: 'none', md: 'block' },
            }}
          >
            <Typography
              noWrap
              sx={{ fontSize: 13, fontWeight: 700 }}
            >
              {user?.name || 'Developer'}
            </Typography>

            <Typography
              noWrap
              sx={{ fontSize: 11, color: 'text.secondary' }}
            >
              {user?.email || ''}
            </Typography>
          </Box>

          <Tooltip title="Logout">
            <IconButton
              onClick={handleLogout}
              size="small"
              aria-label="Logout"
            >
              <LogoutRounded fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          minWidth: 0,
          px: { xs: 2, sm: 3, lg: 5 },
          py: 3,
        }}
      >
        {/* Top bar */}
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: 2,
            mb: 5,
          }}
        >
          <Box>
            <Typography
              sx={{
                color: 'text.secondary',
                fontSize: 12,
                fontWeight: 700,
                letterSpacing: 1,
              }}
            >
              DEVELOPER WORKSPACE / OVERVIEW
            </Typography>

            <Typography
              variant="h4"
              sx={{
                mt: 1,
                fontWeight: 800,
                letterSpacing: '-1px',
              }}
            >
              Good to see you, {user?.name?.split(' ')[0] || 'Developer'}.
            </Typography>

            <Typography
              sx={{ mt: 0.7, color: 'text.secondary' }}
            >
              Here's what's happening in your workspace.
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
            <Tooltip title="Refresh dashboard data">
              <span>
                <IconButton
                  onClick={() => void loadDashboard(true)}
                  disabled={loading || refreshing}
                  sx={{
                    border: '1px solid',
                    borderColor: 'divider',
                    borderRadius: 2,
                  }}
                >
                  {refreshing ? (
                    <CircularProgress size={20} />
                  ) : (
                    <RefreshRounded />
                  )}
                </IconButton>
              </span>
            </Tooltip>

            <Button
              variant="contained"
              startIcon={<AddRounded />}
              onClick={() => setCreateOpen(true)}
            >
              New workflow
            </Button>
          </Box>
        </Box>

        {error && (
          <Alert
            severity="error"
            sx={{ mb: 3 }}
            action={
              <Button
                color="inherit"
                size="small"
                onClick={() => void loadDashboard(true)}
              >
                Retry
              </Button>
            }
          >
            {error}
          </Alert>
        )}

        {/* Statistics */}
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: {
              xs: '1fr',
              sm: 'repeat(2, 1fr)',
              xl: 'repeat(4, 1fr)',
            },
            gap: 2,
            mb: 4,
          }}
        >
          {statCards.map((card) => (
            <Paper
              key={card.label}
              elevation={0}
              sx={{
                ...panelSx,
                p: 2.5,
                transition: 'border-color 180ms ease, transform 180ms ease',
                '&:hover': {
                  borderColor: 'primary.main',
                  transform: 'translateY(-2px)',
                },
              }}
            >
              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <Typography
                  sx={{ color: 'text.secondary', fontSize: 13 }}
                >
                  {card.label}
                </Typography>

                <Box sx={{ color: 'primary.main' }}>
                  {card.icon}
                </Box>
              </Box>

              <Typography
                variant="h3"
                sx={{ fontWeight: 800, mt: 2 }}
              >
                {loading ? '—' : card.value}
              </Typography>

              <Typography
                sx={{
                  color: 'text.secondary',
                  fontSize: 11,
                  mt: 1,
                }}
              >
                {card.detail}
              </Typography>
            </Paper>
          ))}
        </Box>

        {/* Content columns */}
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: {
              xs: '1fr',
              lg: 'minmax(0, 1.5fr) minmax(300px, 1fr)',
            },
            gap: 2.5,
          }}
        >
          {/* Workflows */}
          <Paper
            elevation={0}
            sx={{ ...panelSx, p: { xs: 2, sm: 3 } }}
          >
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                mb: 2.5,
                gap: 2,
              }}
            >
              <Box>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  Your workflows
                </Typography>

                <Typography
                  sx={{ color: 'text.secondary', fontSize: 12, mt: 0.5 }}
                >
                  Your recently created workflows
                </Typography>
              </Box>

              <Chip
                label={`${workflows.length} total`}
                size="small"
                color="primary"
                variant="outlined"
              />
            </Box>

            <Divider sx={{ mb: 1 }} />

            {loading ? (
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  py: 6,
                }}
              >
                <CircularProgress size={28} />

                <Typography
                  sx={{ mt: 2, color: 'text.secondary', fontSize: 13 }}
                >
                  Loading workflows...
                </Typography>
              </Box>
            ) : workflows.length === 0 ? (
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  textAlign: 'center',
                  py: 6,
                  gap: 2,
                }}
              >
                <AccountTreeRounded
                  sx={{ fontSize: 42, color: 'text.secondary' }}
                />

                <Typography sx={{ fontWeight: 700 }}>
                  No workflows yet
                </Typography>

                <Typography
                  sx={{
                    color: 'text.secondary',
                    fontSize: 13,
                    maxWidth: 300,
                  }}
                >
                  Create your first workflow and start automating
                  your development tasks.
                </Typography>

                <Button
                  variant="outlined"
                  startIcon={<AddRounded />}
                  onClick={() => setCreateOpen(true)}
                >
                  Create workflow
                </Button>
              </Box>
            ) : (
              <Box>
                {workflows.map((workflow, index) => (
                  <Box key={workflow.id}>
                    <Box
                      sx={{
                        py: 2,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        gap: 2,
                      }}
                    >
                      <Box
                        sx={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: 1.5,
                          minWidth: 0,
                        }}
                      >
                        <Box
                          sx={{
                            width: 42,
                            height: 42,
                            flexShrink: 0,
                            borderRadius: 2,
                            display: 'grid',
                            placeItems: 'center',
                            backgroundColor: 'action.selected',
                            color: 'primary.main',
                          }}
                        >
                          <AccountTreeRounded />
                        </Box>

                        <Box sx={{ minWidth: 0 }}>
                          <Typography
                            noWrap
                            sx={{ fontWeight: 700, fontSize: 14 }}
                          >
                            {workflow.name}
                          </Typography>

                          <Typography
                            noWrap
                            sx={{
                              color: 'text.secondary',
                              fontSize: 11,
                              mt: 0.5,
                            }}
                          >
                            {workflow.description || 'No description'}
                          </Typography>

                          <Typography
                            sx={{
                              color: 'text.secondary',
                              fontSize: 10,
                              mt: 0.5,
                            }}
                          >
                            Updated {formatDate(workflow.updatedAt)}
                          </Typography>
                        </Box>
                      </Box>

                      <Chip
                        size="small"
                        label={workflow.status}
                        color={getStatusColor(workflow.status)}
                        variant="outlined"
                      />
                    </Box>

                    {index < workflows.length - 1 && <Divider />}
                  </Box>
                ))}
              </Box>
            )}
          </Paper>

          {/* Recent activity */}
          <Paper
            elevation={0}
            sx={{ ...panelSx, p: { xs: 2, sm: 3 } }}
          >
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                mb: 2.5,
              }}
            >
              <Box>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  Recent activity
                </Typography>

                <Typography
                  sx={{ color: 'text.secondary', fontSize: 12, mt: 0.5 }}
                >
                  Latest workflow executions
                </Typography>
              </Box>

              <AccessTimeRounded color="primary" />
            </Box>

            <Divider sx={{ mb: 1 }} />

            {loading ? (
              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'center',
                  py: 6,
                }}
              >
                <CircularProgress size={28} />
              </Box>
            ) : recentExecutions.length === 0 ? (
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  textAlign: 'center',
                  py: 6,
                  gap: 2,
                }}
              >
                <AccessTimeRounded
                  sx={{ fontSize: 38, color: 'text.secondary' }}
                />

                <Typography sx={{ fontWeight: 700 }}>
                  No executions yet
                </Typography>

                <Typography
                  sx={{
                    color: 'text.secondary',
                    fontSize: 13,
                    maxWidth: 260,
                  }}
                >
                  Your workflow execution history will appear here.
                </Typography>
              </Box>
            ) : (
              <Box>
                {recentExecutions.map((execution, index) => (
                  <Box key={execution.id} sx={{ py: 2 }}>
                    <Box
                      sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        gap: 1,
                      }}
                    >
                      <Typography
                        noWrap
                        sx={{ fontWeight: 700, fontSize: 13 }}
                      >
                        {workflowNameById.get(execution.workflowId) ||
                          'Workflow'}
                      </Typography>

                      <Chip
                        size="small"
                        label={execution.status}
                        color={getStatusColor(execution.status)}
                        variant="outlined"
                      />
                    </Box>

                    <Typography
                      sx={{
                        color: 'text.secondary',
                        fontSize: 11,
                        mt: 0.8,
                      }}
                    >
                      {formatDate(execution.startedAt)}
                    </Typography>

                    {execution.durationMs !== null && (
                      <Typography
                        sx={{
                          color: 'text.secondary',
                          fontSize: 11,
                          mt: 0.5,
                        }}
                      >
                        Duration: {execution.durationMs} ms
                      </Typography>
                    )}

                    {index < recentExecutions.length - 1 && (
                      <Divider sx={{ mt: 2 }} />
                    )}
                  </Box>
                ))}
              </Box>
            )}
          </Paper>
        </Box>
      </Box>

      {/* Create workflow dialog */}
      <Dialog
        open={createOpen}
        onClose={() => !creating && setCreateOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle sx={{ fontWeight: 800 }}>
          Create a new workflow
        </DialogTitle>

        <DialogContent>
          <Typography
            sx={{ color: 'text.secondary', fontSize: 13, mb: 2.5 }}
          >
            Give your workflow a name and an optional description.
          </Typography>

          {createError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {createError}
            </Alert>
          )}

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              autoFocus
              fullWidth
              label="Workflow name"
              placeholder="e.g. Deployment automation"
              value={workflowName}
              onChange={(event) => setWorkflowName(event.target.value)}
              slotProps={{
                htmlInput: { maxLength: 100 },
              }}
              required
            />

            <TextField
              fullWidth
              label="Description"
              placeholder="What should this workflow accomplish?"
              value={workflowDescription}
              onChange={(event) =>
                setWorkflowDescription(event.target.value)
              }
              multiline
              minRows={3}
              slotProps={{
                htmlInput: { maxLength: 500 },
              }}
            />
          </Box>
        </DialogContent>

        <DialogActions sx={{ p: 2.5, pt: 0 }}>
          <Button
            color="inherit"
            onClick={() => setCreateOpen(false)}
            disabled={creating}
          >
            Cancel
          </Button>

          <Button
            variant="contained"
            onClick={() => void handleCreateWorkflow()}
            disabled={creating || !workflowName.trim()}
          >
            {creating ? (
              <CircularProgress size={21} color="inherit" />
            ) : (
              'Create workflow'
            )}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DashboardPage;
