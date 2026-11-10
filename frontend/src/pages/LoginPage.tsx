
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

import { loginUser } from '../api/authApi';
import { useAuthStore } from '../store/authStore';

import './AuthPages.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();
    setError('');

    if (!email.trim() || !password) {
      setError('Please enter your email and password.');
      return;
    }

    try {
      setLoading(true);

      const response = await loginUser({
        email: email.trim(),
        password,
      });

      setAuth(response);

      navigate('/dashboard', {
        replace: true,
      });
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
            'Unable to sign in. Please check your credentials and try again.'
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

      {/* Main Authentication Layout */}
      <Box className="auth-main">

        {/* Login Form Section */}
        <Box className="auth-form-section">
          <Box className="auth-form-content">

            {/* Mobile Brand */}
            <Box className="auth-mobile-brand">
              <Box className="auth-brand-icon">
                <AccountTreeRounded />
              </Box>

              <Typography className="auth-brand-name">
                FLOWFORGE<span>.</span>
              </Typography>
            </Box>

            <Typography className="auth-eyebrow">
              <BoltRounded fontSize="small" />
              YOUR WORKSPACE AWAITS
            </Typography>

            <Typography
              component="h1"
              className="auth-heading"
            >
              Welcome
              <br />
              <span>back.</span>
            </Typography>

            <Typography className="auth-description">
              Sign in to manage your workflows, automate
              repetitive tasks, and get back to building.
            </Typography>

            {/* Error Message */}
            {error && (
              <Alert
                severity="error"
                onClose={() => setError('')}
                className="auth-alert"
              >
                {error}
              </Alert>
            )}

            {/* Login Form */}
            <Box
              component="form"
              onSubmit={handleSubmit}
              noValidate
            >
              <Stack spacing={2.5}>

                {/* Email Field */}
                <Box>
                  <Typography className="auth-field-label">
                    EMAIL ADDRESS
                  </Typography>

                  <TextField
                    fullWidth
                    type="email"
                    placeholder="you@example.com"
                    value={email}
                    onChange={(event) =>
                      setEmail(event.target.value)
                    }
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

                {/* Password Field */}
                <Box>
                  <Typography className="auth-field-label">
                    PASSWORD
                  </Typography>

                  <TextField
                    fullWidth
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Enter your password"
                    value={password}
                    onChange={(event) =>
                      setPassword(event.target.value)
                    }
                    autoComplete="current-password"
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
                                setShowPassword(
                                  (previous) => !previous
                                )
                              }
                              edge="end"
                              aria-label={
                                showPassword
                                  ? 'Hide password'
                                  : 'Show password'
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

                {/* Submit Button */}
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  fullWidth
                  disabled={loading}
                  endIcon={
                    loading
                      ? undefined
                      : <ArrowForwardRounded />
                  }
                  className="auth-submit-button"
                >
                  {loading ? (
                    <CircularProgress
                      size={22}
                      color="inherit"
                    />
                  ) : (
                    'Sign in to FlowForge'
                  )}
                </Button>

              </Stack>
            </Box>

            {/* Registration Link */}
            <Divider className="auth-divider">
              <Typography className="auth-divider-text">
                NEW TO FLOWFORGE?
              </Typography>
            </Divider>

            <Button
              component={Link}
              to="/register"
              variant="outlined"
              fullWidth
              className="auth-secondary-button"
              disabled={loading}
            >
              Create an account
            </Button>

            <Typography className="auth-footer">
              By continuing, you agree to use FlowForge responsibly.
            </Typography>

          </Box>
        </Box>

        {/* Branding / Visual Section */}
        <Box className="auth-visual-section">
          <Box className="auth-visual-grid" />

          <Box className="auth-visual-content">

            <Box className="auth-visual-top">
              <Typography className="auth-visual-label">
                <AutoAwesomeRounded fontSize="small" />
                BUILT FOR DEVELOPERS
              </Typography>

              <Typography className="auth-visual-index">
                PLATFORM / 001
              </Typography>
            </Box>

            {/* Hero Heading */}
            <Box className="auth-visual-headline">
              <Typography component="h2">
                Less busywork.
              </Typography>

              <Typography component="h2">
                More <span>building.</span>
              </Typography>
            </Box>

            <Typography className="auth-visual-description">
              Connect your tools, design powerful workflows,
              and let automation handle the repetitive stuff.
            </Typography>

            {/* Feature List */}
            <Box className="auth-feature-list">

              <Box className="auth-feature">
                <Box className="auth-feature-icon">
                  <HubRounded />
                </Box>

                <Box>
                  <Typography className="auth-feature-title">
                    Visual workflow builder
                  </Typography>

                  <Typography className="auth-feature-description">
                    Design and connect tasks on one canvas.
                  </Typography>
                </Box>
              </Box>

              <Box className="auth-feature">
                <Box className="auth-feature-icon">
                  <AutoAwesomeRounded />
                </Box>

                <Box>
                  <Typography className="auth-feature-title">
                    AI-powered generation
                  </Typography>

                  <Typography className="auth-feature-description">
                    Turn plain-language ideas into workflows.
                  </Typography>
                </Box>
              </Box>

              <Box className="auth-feature">
                <Box className="auth-feature-icon">
                  <CodeRounded />
                </Box>

                <Box>
                  <Typography className="auth-feature-title">
                    Developer-first control
                  </Typography>

                  <Typography className="auth-feature-description">
                    Inspect, configure, execute and monitor.
                  </Typography>
                </Box>
              </Box>

            </Box>

            {/* Footer */}
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

export default LoginPage;
