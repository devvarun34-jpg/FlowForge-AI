
import { useState, type FormEvent } from 'react';
import axios from 'axios';

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Divider,
  IconButton,
  InputAdornment,
  Stack,
  TextField,
  Typography,
} from '@mui/material';

import {
  AccountTreeRounded,
  ArrowForwardRounded,
  AutoAwesomeRounded,
  VisibilityOutlined,
  VisibilityOffOutlined,
  BoltRounded,
  CodeRounded,
  HubRounded,
} from '@mui/icons-material';

import { Link, useNavigate } from 'react-router-dom';

import { registerUser } from '../api/authApi';

import './AuthPages.css';

const RegisterPage = () => {
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError('');
    setSuccess('');

    if (!name.trim() || !email.trim() || !password || !confirmPassword) {
      setError('Please complete all fields.');
      return;
    }

    if (password.length < 8) {
      setError('Password must contain at least 8 characters.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    try {
      setLoading(true);

      await registerUser({
        name: name.trim(),
        email: email.trim(),
        password,
      });

      setSuccess('Your account has been created. Redirecting to login...');

      // Registration does not return a JWT.
      // The user must log in separately.
      window.setTimeout(() => {
        navigate('/login', {
          replace: true,
          state: {
            registered: true,
            email: email.trim(),
          },
        });
      }, 1200);

    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        const responseData = err.response?.data as
          | {
              message?: string;
              error?: string;
            }
          | undefined;

        setError(
          responseData?.message ||
            responseData?.error ||
            'Unable to create your account. Please try again.'
        );
      } else {
        setError('Something went wrong. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box className="auth-page">

      {/* Top Navigation */}
      <Box className="auth-topbar">
        <Link to="/login" className="auth-brand">
          <Box className="auth-brand-icon">
            <AccountTreeRounded />
          </Box>

          <Typography className="auth-brand-name">
            FLOWFORGE<span>.</span>
          </Typography>
        </Link>

        <Typography className="auth-topbar-caption">
          DEVELOPER WORKFLOW AUTOMATION
        </Typography>
      </Box>

      <Box className="auth-main">

        {/* Registration Form */}
        <Box className="auth-form-section">
          <Box className="auth-form-content">

            <Typography className="auth-eyebrow">
              <BoltRounded fontSize="small" />
              START BUILDING SOMETHING
            </Typography>

            <Typography
              component="h1"
              className="auth-heading"
            >
              Create
              <br />
              <span>account.</span>
            </Typography>

            <Typography className="auth-description">
              Set up your workspace and bring your development
              workflows together in one place.
            </Typography>

            {error && (
              <Alert
                severity="error"
                onClose={() => setError('')}
                className="auth-alert"
              >
                {error}
              </Alert>
            )}

            {success && (
              <Alert
                severity="success"
                className="auth-alert"
              >
                {success}
              </Alert>
            )}

            <Box
              component="form"
              onSubmit={handleSubmit}
              noValidate
            >
              <Stack spacing={2}>

                {/* Full Name */}
                <Box>
                  <Typography className="auth-field-label">
                    FULL NAME
                  </Typography>

                  <TextField
                    fullWidth
                    type="text"
                    placeholder="Your name"
                    value={name}
                    onChange={(event) => setName(event.target.value)}
                    autoComplete="name"
                    required
                    disabled={loading}
                    slotProps={{
                      htmlInput: {
                        'aria-label': 'Full name',
                      },
                    }}
                  />
                </Box>

                {/* Email */}
                <Box>
                  <Typography className="auth-field-label">
                    EMAIL ADDRESS
                  </Typography>

                  <TextField
                    fullWidth
                    type="email"
                    placeholder="you@example.com"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    autoComplete="email"
                    required
                    disabled={loading}
                    slotProps={{
                      htmlInput: {
                        'aria-label': 'Email address',
                      },
                    }}
                  />
                </Box>

                {/* Password */}
                <Box>
                  <Typography className="auth-field-label">
                    PASSWORD
                  </Typography>

                  <TextField
                    fullWidth
                    type={showPassword ? 'text' : 'password'}
                    placeholder="At least 8 characters"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    autoComplete="new-password"
                    required
                    disabled={loading}
                    slotProps={{
                      htmlInput: {
                        'aria-label': 'Password',
                      },
                      input: {
                        endAdornment: (
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() =>
                                setShowPassword((previous) => !previous)
                              }
                              edge="end"
                              aria-label={
                                showPassword ? 'Hide password' : 'Show password'
                              }
                            >
                              {showPassword ? (
                                <VisibilityOffOutlined />
                              ) : (
                                <VisibilityOutlined />
                              )}
                            </IconButton>
                          </InputAdornment>
                        ),
                      },
                    }}
                  />
                </Box>

                {/* Confirm Password */}
                <Box>
                  <Typography className="auth-field-label">
                    CONFIRM PASSWORD
                  </Typography>

                  <TextField
                    fullWidth
                    type={showConfirmPassword ? 'text' : 'password'}
                    placeholder="Re-enter your password"
                    value={confirmPassword}
                    onChange={(event) =>
                      setConfirmPassword(event.target.value)
                    }
                    autoComplete="new-password"
                    required
                    disabled={loading}
                    slotProps={{
                      htmlInput: {
                        'aria-label': 'Confirm password',
                      },
                      input: {
                        endAdornment: (
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() =>
                                setShowConfirmPassword(
                                  (previous) => !previous
                                )
                              }
                              edge="end"
                              aria-label={
                                showConfirmPassword
                                  ? 'Hide confirm password'
                                  : 'Show confirm password'
                              }
                            >
                              {showConfirmPassword ? (
                                <VisibilityOffOutlined />
                              ) : (
                                <VisibilityOutlined />
                              )}
                            </IconButton>
                          </InputAdornment>
                        ),
                      },
                    }}
                  />
                </Box>

                {/* Submit */}
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  fullWidth
                  disabled={loading}
                  endIcon={
                    loading ? undefined : <ArrowForwardRounded />
                  }
                  className="auth-submit-button"
                >
                  {loading ? (
                    <CircularProgress size={22} color="inherit" />
                  ) : (
                    'Create your account'
                  )}
                </Button>

              </Stack>
            </Box>

            <Divider className="auth-divider">
              <Typography className="auth-divider-text">
                ALREADY HAVE AN ACCOUNT?
              </Typography>
            </Divider>

            <Button
              component={Link}
              to="/login"
              variant="outlined"
              fullWidth
              className="auth-secondary-button"
              disabled={loading}
            >
              Sign in instead
            </Button>

            <Typography className="auth-footer">
              Your workspace. Your workflows. Your control.
            </Typography>

          </Box>
        </Box>

        {/* Branding Section */}
        <Box className="auth-visual-section">
          <Box className="auth-visual-grid" />

          <Box className="auth-visual-content">

            <Box className="auth-visual-top">
              <Typography className="auth-visual-label">
                <AutoAwesomeRounded fontSize="small" />
                BUILT FOR DEVELOPERS
              </Typography>

              <Typography className="auth-visual-index">
                PLATFORM / 002
              </Typography>
            </Box>

            <Box className="auth-visual-headline">
              <Typography component="h2">
                Your ideas.
              </Typography>

              <Typography component="h2">
                Your <span>workflows.</span>
              </Typography>
            </Box>

            <Typography className="auth-visual-description">
              Stop switching between disconnected tools.
              Build, automate and monitor your development
              workflows from a single workspace.
            </Typography>

            <Box className="auth-feature-list">

              <Box className="auth-feature">
                <Box className="auth-feature-icon">
                  <HubRounded />
                </Box>

                <Box>
                  <Typography className="auth-feature-title">
                    Connect everything
                  </Typography>

                  <Typography className="auth-feature-description">
                    Bring your workflow steps into one visual canvas.
                  </Typography>
                </Box>
              </Box>

              <Box className="auth-feature">
                <Box className="auth-feature-icon">
                  <AutoAwesomeRounded />
                </Box>

                <Box>
                  <Typography className="auth-feature-title">
                    Let AI do the groundwork
                  </Typography>

                  <Typography className="auth-feature-description">
                    Generate workflows from natural-language instructions.
                  </Typography>
                </Box>
              </Box>

              <Box className="auth-feature">
                <Box className="auth-feature-icon">
                  <CodeRounded />
                </Box>

                <Box>
                  <Typography className="auth-feature-title">
                    Stay in control
                  </Typography>

                  <Typography className="auth-feature-description">
                    Review, approve and execute your workflows.
                  </Typography>
                </Box>
              </Box>

            </Box>

            <Box className="auth-visual-bottom">
              <Typography>
                DESIGN. AUTOMATE. SHIP.
              </Typography>

              <Typography>
                FLOWFORGE AI © 2026
              </Typography>
            </Box>

          </Box>
        </Box>

      </Box>
    </Box>
  );
};

export default RegisterPage;
