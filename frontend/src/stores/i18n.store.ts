import { defineStore } from 'pinia';
import I18nService from '../services/i18n.service';

export const useI18nStore = defineStore('i18n', {
  state: () => ({
    currentLang: 'zh-CN',
    languagePack: {} as Record<string, string>,
    loading: false
  }),
  actions: {
    async loadLanguagePack(lang: string) {
      this.loading = true;
      try {
        const response = await I18nService.getLanguagePack(lang);
        this.languagePack = response.data;
        this.currentLang = lang;
      } catch (error) {
        console.error('Failed to load language pack', error);
      } finally {
        this.loading = false;
      }
    },

    setLanguage(lang: string) {
        this.loadLanguagePack(lang);
    },
    
    t(key: string) {
      return this.languagePack[key] || key;
    }
  }
});
