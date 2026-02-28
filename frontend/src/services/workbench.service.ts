import apiClient from './api';

export default {
  getManagerOverview(storeId: number) {
    return apiClient.get(`/workbench/manager/overview?storeId=${storeId}`);
  },
  
  getFrontDeskTasks() {
    return apiClient.get('/workbench/frontdesk/tasks');
  },
  
  getTechnicianTasks() {
    return apiClient.get('/workbench/technician/tasks');
  },

  getSecurityTasks() {
    return apiClient.get('/workbench/security/tasks');
  }
};
