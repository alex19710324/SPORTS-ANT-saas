import { defineStore } from 'pinia';
import MarketingService from '../services/marketing.service';

export const useMarketingStore = defineStore('marketing', {
  state: () => ({
    activities: [] as any[],
    generatedContent: null as any,
    loading: false
  }),
  actions: {
    async createActivity(activity: any) {
      this.loading = true;
      try {
        await MarketingService.createActivity(activity);
        await this.fetchActivities(); // Refresh list
      } catch (error) {
        console.error('Failed to create activity', error);
      } finally {
        this.loading = false;
      }
    },
    
    async fetchActivities() {
      this.loading = true;
      try {
        const response = await MarketingService.listActivities();
        this.activities = response.data;
      } catch (error) {
        console.error('Failed to fetch activities', error);
      } finally {
        this.loading = false;
      }
    },
    
    async generateContent(activityId: number) {
      this.loading = true;
      try {
        const response = await MarketingService.generateContent(activityId);
        this.generatedContent = response.data;
      } catch (error) {
        console.error('Failed to generate content', error);
      } finally {
        this.loading = false;
      }
    }
  }
});
