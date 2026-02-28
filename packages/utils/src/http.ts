import axios, { type AxiosInstance, type AxiosResponse } from 'axios';
import { API_BASE_URL, TIMEOUT } from './constants';
import { AppError } from './errors';

// Create Axios Instance
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('user-token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // Customize success handling if needed
    return response;
  },
  (error) => {
    // Unified Error Handling
    if (error.response) {
      const { status, data } = error.response;
      // You can dispatch actions or show notifications here
      // For now, we throw a custom error
      throw new AppError(data.message || 'Server Error', status);
    } else if (error.request) {
      throw new AppError('Network Error: No response received', 0);
    } else {
      throw new AppError(error.message || 'Request Setup Error', -1);
    }
  }
);

export default apiClient;
