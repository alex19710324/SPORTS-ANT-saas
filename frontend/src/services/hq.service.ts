import apiClient from './api';

export default {
  getGlobalOverview() {
    return apiClient.get('/hq/dashboard/overview');
  },
  
  getStoreMapData() {
    return apiClient.get('/hq/map/stores');
  },
  
  getFranchiseApplications() {
    return apiClient.get('/hq/franchise/applications');
  },

  submitFranchiseApplication(app: any) {
    return apiClient.post('/hq/franchise/apply', app);
  },
  
  approveFranchiseApplication(payload: any) {
    return apiClient.post('/hq/franchise/approve', payload);
  }
};
