import { defineStore } from 'pinia';
import DataService from '../services/data.service';

export const useDataStore = defineStore('data', {
  state: () => ({
    metrics: {} as Record<string, any>,
    tagResults: null as any,
    loading: false
  }),
  actions: {
    async fetchMetric(metric: string) {
      try {
        const response = await DataService.getRealTimeMetric(metric);
        this.metrics[metric] = response.data;
      } catch (error) {
        console.error(`Failed to fetch metric ${metric}`, error);
      }
    },
    
    async queryTags(tags: string[]) {
      this.loading = true;
      try {
        const response = await DataService.queryTags(tags);
        this.tagResults = response.data;
      } catch (error) {
        console.error('Failed to query tags', error);
      } finally {
        this.loading = false;
      }
    }
  }
});
