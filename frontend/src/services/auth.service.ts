import apiClient from './api';

export default {
  login(user: any) {
    return apiClient.post('/auth/signin', {
      username: user.username,
      password: user.password
    });
  },

  register(user: any) {
    return apiClient.post('/auth/signup', {
      username: user.username,
      email: user.email,
      password: user.password
    });
  }
};
