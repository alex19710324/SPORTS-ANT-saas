import apiClient from './api';

export default {
  getManagerOverview(storeId: number) {
    return apiClient.get(`/workbench/manager/overview?storeId=${storeId}`);
  },
  
  getFrontDeskTasks() {
    return apiClient.get('/workbench/frontdesk/overview');
  },
  
  checkInMember(memberCode: string) {
    return apiClient.post('/workbench/frontdesk/checkin', { memberCode });
  },

  getTechnicianTasks() {
    return apiClient.get('/workbench/technician/overview');
  },

  getSecurityTasks() {
    return apiClient.get('/workbench/security/tasks');
  }
};
