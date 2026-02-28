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

// Import all local locale files
const localLocales: Record<string, any> = import.meta.glob('./locales/*.json', { eager: true });

export async function loadLanguageAsync(lang: string) {
    // Check if the language is RTL (Arabic)
    const isRTL = lang.startsWith('ar');
    document.documentElement.dir = isRTL ? 'rtl' : 'ltr';

    // 1. Try to load from local files first (Fastest)
    const localFileKey = `./locales/${lang}.json`;
    if (localLocales[localFileKey]) {
        // @ts-ignore
        const localMessages = localLocales[localFileKey].default || localLocales[localFileKey];
        
        // Use local messages immediately
        // @ts-ignore
        if (!i18n.global.availableLocales.includes(lang)) {
             // @ts-ignore
             i18n.global.setLocaleMessage(lang, localMessages);
        }
    }

    // 2. Try to fetch updates from backend (Async upgrade)
    try {
        const response = await apiClient.get(`/language/${lang}`);
        if (response.data && response.data.content) {
             const remoteMessages = JSON.parse(response.data.content);
             // Merge or overwrite local messages
             // @ts-ignore
             i18n.global.setLocaleMessage(lang, remoteMessages);
             console.log(`Language ${lang} updated from backend.`);
        }
    } catch (error) {
        console.warn(`Failed to fetch language update for ${lang}, using local fallback.`, error);
        // If local also missing, then fallback to en-US
        if (!localLocales[localFileKey] && lang !== 'en-US') {
            return loadLanguageAsync('en-US');
        }
    }
    
    setLanguage(lang);
    return lang;
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
    let targetLang = 'zh-CN'; // Default is Chinese
    
    if (browserLang) {
        if (browserLang.startsWith('zh')) targetLang = 'zh-CN';
        else if (browserLang.startsWith('en')) targetLang = 'en-US';
        else if (browserLang.startsWith('fr')) targetLang = 'fr-FR';
        else if (browserLang.startsWith('it')) targetLang = 'it-IT';
        else if (browserLang.startsWith('de')) targetLang = 'de-DE';
        else if (browserLang.startsWith('es')) targetLang = 'es-ES';
        else if (browserLang.startsWith('pt')) targetLang = 'pt-PT';
        else if (browserLang.startsWith('ar')) targetLang = 'ar-SA';
    }
    
    await loadLanguageAsync(targetLang);

  } catch (error) {
    console.warn('Language detection failed, using default.', error);
    await loadLanguageAsync('zh-CN');
  }
}

export default i18n;
