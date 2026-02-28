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
    }
  }
});
