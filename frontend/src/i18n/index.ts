import { createI18n } from 'vue-i18n';
import apiClient from '../services/api';

const i18n = createI18n({
  legacy: false,
  locale: 'zh-CN', 
  fallbackLocale: 'en-US',
  messages: {
    // Initial empty, will be loaded dynamically
  },
});

export async function loadLanguageAsync(lang: string) {
    // @ts-ignore
    if (i18n.global.availableLocales.includes(lang)) {
        setLanguage(lang);
        return lang;
    }

    try {
        const response = await apiClient.get(`/language/${lang}`);
        const messages = JSON.parse(response.data.content);
        
        // @ts-ignore
        i18n.global.setLocaleMessage(lang, messages);
        
        setLanguage(lang);
        return lang;
    } catch (error) {
        console.error(`Failed to load language: ${lang}`, error);
        // Fallback to English if loading failed and not already in English
        if (lang !== 'en-US') {
             return loadLanguageAsync('en-US');
        }
        return 'en-US';
    }
}

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
      await loadLanguageAsync(storedLang);
      return;
    }

    // 2. Check browser language
    const browserLang = navigator.language;
    let targetLang = 'zh-CN'; // Default
    
    if (browserLang) {
        if (browserLang.startsWith('zh')) {
            targetLang = 'zh-CN';
        } else if (browserLang.startsWith('en')) {
            targetLang = 'en-US';
        }
    }
    
    // 3. (Optional) Call backend detection API if needed for IP based
    // const response = await apiClient.get('/language/detect');
    // if (response.data && response.data.lang) targetLang = response.data.lang;
    
    await loadLanguageAsync(targetLang);

  } catch (error) {
    console.warn('Language detection failed, using default.', error);
    await loadLanguageAsync('zh-CN');
  }
}

export default i18n;
