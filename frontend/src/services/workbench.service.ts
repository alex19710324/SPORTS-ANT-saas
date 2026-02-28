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

  registerMember(name: string, phone: string) {
    return apiClient.post('/workbench/frontdesk/register', { name, phone });
  },

  processSale(memberCode: string, amount: number) {
    return apiClient.post('/workbench/frontdesk/sale', { memberCode, amount });
  },

  getTechnicianTasks() {
    return apiClient.get('/workbench/technician/overview');
  },

  updateWorkOrderStatus(orderId: number, status: string) {
    return apiClient.put(`/workbench/technician/work-orders/${orderId}/status`, { status });
  },

  approveRequest(requestId: number) {
    return apiClient.put(`/workbench/manager/approvals/${requestId}/approve`, {});
  },

  getSecurityTasks() {
    return apiClient.get('/workbench/security/tasks');
  }
};
