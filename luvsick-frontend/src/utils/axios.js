import axios from 'axios';

// Helper function to get cookie value by name
const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
};

// Create axios instance with base URL
const instance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true
});

// List of endpoints that should not include CSRF token
const ignoredEndpoints = [
  '/api/v1/auth/login'
];

// Request interceptor
instance.interceptors.request.use(
  (config) => {
    // Skip CSRF token for ignored endpoints
    if (ignoredEndpoints.some(endpoint => config.url.includes(endpoint))) {
      return config;
    }

    // Set CSRF token for non-GET requests
    if (config.method && config.method.toLowerCase() !== 'get') {
      const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
      if (match) {
        config.headers['X-XSRF-TOKEN'] = decodeURIComponent(match[1]);
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    if (error.response?.status === 403) {
      // Redirect to login page on 403 errors
      window.location.href = '/login';
      return Promise.reject(error);
    }
    return Promise.reject(error);
  }
);

export default instance; 