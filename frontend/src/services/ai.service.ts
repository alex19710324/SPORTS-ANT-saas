import apiClient from './api';

export default {
  getSystemHealth() {
    return apiClient.get('/ai/brain/health');
  },

  getSuggestions() {
    return apiClient.get('/ai/brain/suggestions');
  },

  provideFeedback(suggestionId: number, accepted: boolean, feedback: string) {
    return apiClient.post(`/ai/brain/feedback/${suggestionId}`, {
      accepted,
      feedback
    });
  }
};
