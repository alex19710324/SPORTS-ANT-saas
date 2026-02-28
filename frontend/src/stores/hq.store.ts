import { defineStore } from 'pinia';
import HQService from '../services/hq.service';

export const useHQStore = defineStore('hq', {
  state: () => ({
    overview: null as any,
    storeMapData: [] as any[],
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
    }
  }
});
