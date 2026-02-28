import { defineStore } from 'pinia';
import WorkbenchService from '../services/workbench.service';

export const useWorkbenchStore = defineStore('workbench', {
  state: () => ({
    managerOverview: null as any,
    frontDeskTasks: null as any,
    technicianTasks: null as any,
    securityTasks: null as any,
    loading: false
  }),
  actions: {
    async fetchManagerOverview(storeId: number) {
      this.loading = true;
      try {
        const response = await WorkbenchService.getManagerOverview(storeId);
        this.managerOverview = response.data;
      } catch (error) {
        console.error('Failed to fetch manager overview', error);
      } finally {
        this.loading = false;
      }
    },
    async fetchFrontDeskTasks() {
      this.loading = true;
      try {
        const response = await WorkbenchService.getFrontDeskTasks();
        this.frontDeskTasks = response.data;
      } catch (error) {
        console.error('Failed to fetch front desk tasks', error);
      } finally {
        this.loading = false;
      }
    },
    async checkInMember(code: string) {
        try {
            return await WorkbenchService.checkInMember(code);
        } catch (error) {
            throw error;
        }
    },
    async registerMember(name: string, phone: string) {
        try {
            return await WorkbenchService.registerMember(name, phone);
        } catch (error) {
            throw error;
        }
    },
    async processSale(code: string, amount: number) {
        try {
            return await WorkbenchService.processSale(code, amount);
        } catch (error) {
            throw error;
        }
    },
    async fetchTechnicianTasks() {
      this.loading = true;
      try {
        const response = await WorkbenchService.getTechnicianTasks();
        this.technicianTasks = response.data;
      } catch (error) {
        console.error('Failed to fetch technician tasks', error);
      } finally {
        this.loading = false;
      }
    },
    async updateWorkOrderStatus(orderId: number, status: string) {
        try {
            await WorkbenchService.updateWorkOrderStatus(orderId, status);
            await this.fetchTechnicianTasks(); // Refresh
        } catch (error) {
            throw error;
        }
    },
    async approveRequest(requestId: number) {
        try {
            await WorkbenchService.approveRequest(requestId);
            await this.fetchManagerOverview(1); // Refresh
        } catch (error) {
            throw error;
        }
    },
    async getUnreadMessageCount() {
        try {
            const res = await WorkbenchService.getUnreadCount();
            return res.data;
        } catch (error) {
            return 0;
        }
    },
    async fetchSecurityTasks() {
      this.loading = true;
      try {
        const response = await WorkbenchService.getSecurityTasks();
        this.securityTasks = response.data;
      } catch (error) {
        console.error('Failed to fetch security tasks', error);
      } finally {
        this.loading = false;
      }
    },
    async createIncident(type: string, location: string, description: string) {
        try {
            await WorkbenchService.createIncident(type, location, description);
            await this.fetchSecurityTasks();
        } catch (error) {
            throw error;
        }
    },
    async resolveIncident(id: number) {
        try {
            await WorkbenchService.resolveIncident(id);
            await this.fetchSecurityTasks();
        } catch (error) {
            throw error;
        }
    }
  }
});
