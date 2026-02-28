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

  getMessages() {
    return apiClient.get('/communication/messages/inbox');
  },
  
  getUnreadCount() {
    return apiClient.get('/communication/messages/unread-count');
  },
  
  sendMessage(receiverId: number, content: string) {
    return apiClient.post('/communication/messages/send', { receiverId, content });
  },

  getSecurityTasks() {
    return apiClient.get('/safety/tasks');
  },
  
  createIncident(type: string, location: string, description: string) {
    return apiClient.post('/safety/incidents', { type, location, description });
  },
  
  resolveIncident(id: number) {
    return apiClient.post(`/safety/incidents/${id}/resolve`);
  }
};
