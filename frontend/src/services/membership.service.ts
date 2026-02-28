import apiClient from './api';

export default {
  getMyMembership() {
    return apiClient.get('/membership/me');
  },
  
  // Admin only - for demo
  addGrowth(userId: number, amount: number, source: string) {
    return apiClient.post('/membership/growth/add', { userId, amount, source });
  }
};
