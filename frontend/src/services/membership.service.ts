import apiClient from './api';

export default {
  getMyMembership() {
    return apiClient.get('/membership/me');
  },
  
  // User Actions
  dailyCheckIn() {
    return apiClient.post('/membership/checkin');
  },

  simulatePurchase(amount: number) {
    return apiClient.post('/membership/simulate-purchase', { amount });
  },

  // Admin only - for demo
  addGrowth(userId: number, amount: number, source: string) {
    return apiClient.post('/membership/growth/add', { userId, amount, source });
  }
};
