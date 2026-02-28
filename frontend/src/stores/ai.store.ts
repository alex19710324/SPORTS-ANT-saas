import { defineStore } from 'pinia';
import AiService from '../services/ai.service';

export const useAiStore = defineStore('ai', {
  state: () => ({
    health: {
      status: 'UNKNOWN',
      memory_usage_percent: '0',
      cpu_load: '0',
      active_threats: 0,
      ai_guardian_message: 'Initializing...'
    },
    suggestions: [] as any[],
    loading: false,
    error: null as any
  }),
  actions: {
    async fetchHealth() {
      try {
        const response = await AiService.getSystemHealth();
        this.health = response.data;
      } catch (error: any) {
        console.error('Failed to fetch AI health', error);
      }
    },
    async fetchSuggestions() {
      try {
        const response = await AiService.getSuggestions();
        this.suggestions = response.data;
      } catch (error: any) {
        console.error('Failed to fetch AI suggestions', error);
      }
    },
    async handleSuggestion(id: number, accepted: boolean, feedback: string) {
        try {
            await AiService.provideFeedback(id, accepted, feedback);
            // Refresh list
            await this.fetchSuggestions();
        } catch (error: any) {
            console.error('Failed to provide feedback', error);
        }
    }
  }
});
