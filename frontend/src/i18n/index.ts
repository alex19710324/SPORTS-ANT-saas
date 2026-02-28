import { createI18n } from 'vue-i18n';
import zhCN from './locales/zh-CN.json';
import enUS from './locales/en-US.json';
import apiClient from '../services/api';

const i18n = createI18n({
  legacy: false,
  locale: 'zh-CN', 
  fallbackLocale: 'en-US',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
});

export function setLanguage(lang: string) {
    // @ts-ignore
    if (i18n.global.locale.value) {
        // @ts-ignore
        i18n.global.locale.value = lang;
    } else {
        // @ts-ignore
        i18n.global.locale = lang;
    }
    
    localStorage.setItem('user-language', lang);
    document.querySelector('html')?.setAttribute('lang', lang);
    apiClient.defaults.headers.common['Accept-Language'] = lang;
}

export async function detectAndSetLanguage() {
  try {
    // 1. Check local storage
    const storedLang = localStorage.getItem('user-language');
    if (storedLang) {
      setLanguage(storedLang);
      return;
    }

    // 2. Call backend detection API
    const response = await apiClient.get('/language/detect');
    if (response.data && response.data.lang) {
        setLanguage(response.data.lang);
    }
  } catch (error) {
    console.warn('Language detection failed, using default.', error);
  }
}

export default i18n;
