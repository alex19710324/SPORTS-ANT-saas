import apiClient from './api';

export default {
  getLanguagePack(lang: string) {
    return apiClient.get(`/i18n/pack/${lang}`);
  },
  
  createKey(key: any) {
    return apiClient.post('/i18n/keys', key);
  },
  
  autoTranslate(id: number) {
    return apiClient.post(`/i18n/keys/${id}/auto-translate`);
  }
};
