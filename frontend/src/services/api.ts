import { apiClient, AuthError } from '@sportsant/utils';

// Re-configure interceptors to match frontend specific logic (like redirection)
apiClient.interceptors.request.use(
  (config: any) => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        const user = JSON.parse(userStr);
        if (user && user.token) {
          config.headers.Authorization = 'Bearer ' + user.token;
        }
    }
    return config;
  }
);

apiClient.interceptors.response.use(
  (response: any) => response,
  (error: any) => {
    if (error.status === 401 && window.location.pathname !== '/login') {
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
