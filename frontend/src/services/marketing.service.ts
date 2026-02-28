import apiClient from './api';

export default {
  createActivity(activity: any) {
    return apiClient.post('/marketing/activities', activity);
  },
  
  listActivities() {
    return apiClient.get('/marketing/activities');
  },
  
  generateContent(activityId: number) {
    return apiClient.post(`/marketing/activities/${activityId}/generate-content`);
  }
};
