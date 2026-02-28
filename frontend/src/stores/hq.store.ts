import { defineStore } from 'pinia';
import HQService from '../services/hq.service';

export const useHQStore = defineStore('hq', {
  state: () => ({
    overview: null as any,
    storeMapData: [] as any[],
    franchiseApplications: [] as any[],
    loading: false
  }),
  actions: {
    async fetchGlobalOverview() {
      this.loading = true;
      try {
        const response = await HQService.getGlobalOverview();
        this.overview = response.data;
      } catch (error) {
        console.error('Failed to fetch HQ overview', error);
      } finally {
        this.loading = false;
      }
    },
    async fetchStoreMapData() {
      try {
        const response = await HQService.getStoreMapData();
        this.storeMapData = response.data.stores;
      } catch (error) {
        console.error('Failed to fetch store map data', error);
      }
    },
    async fetchFranchiseApplications() {
      try {
        const response = await HQService.getFranchiseApplications();
        this.franchiseApplications = response.data.applications;
      } catch (error) {
        console.error('Failed to fetch franchise applications', error);
      }
    },
    async approveApplication(appId: number, approve: boolean, comments: string) {
      try {
        await HQService.approveFranchiseApplication({ applicationId: appId, approve, comments });
        await this.fetchFranchiseApplications();
      } catch (error) {
        console.error('Failed to approve application', error);
      }
    }
  }
});
