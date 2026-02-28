import apiClient from './api';

export default {
  createActivity(activity: any) {
    return apiClient.post('/marketing/campaigns', activity);
  },
  
  listActivities() {
    return apiClient.get('/marketing/campaigns');
  },
  
  generateContent(activityId: number) {
    return apiClient.post(`/marketing/campaigns/${activityId}/ai-content`);
  }
};
