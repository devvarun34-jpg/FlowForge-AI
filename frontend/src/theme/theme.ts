
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',

    primary: {
      main: '#00C2D6',
      light: '#66DFE9',
      dark: '#008E9E',
      contrastText: '#101316',
    },

    secondary: {
      main: '#9AA4B2',
    },

    background: {
      default: '#17191D',
      paper: '#22262D',
    },

    text: {
      primary: '#F5F7FA',
      secondary: '#9AA4B2',
    },

    success: {
      main: '#35C98A',
    },

    warning: {
      main: '#F5B942',
    },

    error: {
      main: '#FF5C70',
    },

    divider: '#353B44',
  },

  typography: {
    fontFamily: '"Inter", "Roboto", sans-serif',

    h1: {
      fontWeight: 800,
      letterSpacing: '-0.055em',
    },

    h2: {
      fontWeight: 750,
      letterSpacing: '-0.045em',
    },

    h3: {
      fontWeight: 700,
      letterSpacing: '-0.035em',
    },

    button: {
      textTransform: 'none',
      fontWeight: 650,
    },
  },

  shape: {
    borderRadius: 10,
  },

  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 18px',
          transition: 'all 180ms ease',
        },
      },

      variants: [
        {
          props: {
            variant: 'contained',
            color: 'primary',
          },

          style: {
            boxShadow: '0 0 0 rgba(0,194,214,0)',

            '&:hover': {
              backgroundColor: '#66DFE9',
              boxShadow: '0 0 24px rgba(0,194,214,0.2)',
              transform: 'translateY(-1px)',
            },
          },
        },
      ],
    },

    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          border: '1px solid #353B44',
        },
      },
    },

    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: 8,

          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: '#00C2D6',
            borderWidth: 1,
          },
        },
      },
    },
  },
});

export default theme;
