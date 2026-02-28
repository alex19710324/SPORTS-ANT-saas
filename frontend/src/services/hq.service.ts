import apiClient from './api';

export default {
  getGlobalOverview() {
    return apiClient.get('/hq/overview');
  },
  
  getStoreMapData() {
    return apiClient.get('/hq/stores');
  },
  
  getFranchiseApplications() {
    return apiClient.get('/hq/applications');
  },

  submitFranchiseApplication(app: any) {
    return apiClient.post('/hq/franchise/apply', app);
  },
  
  approveFranchiseApplication(id: number) {
    return apiClient.post(`/hq/applications/${id}/approve`);
  }
};
