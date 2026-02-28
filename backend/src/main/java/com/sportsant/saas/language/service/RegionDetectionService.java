package com.sportsant.saas.language.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RegionDetectionService {

    public Locale detectRegion(HttpServletRequest request) {
        // 1. Check for custom header (e.g., from user settings)
        String customLang = request.getHeader("X-User-Language");
        if (customLang != null && !customLang.isEmpty()) {
            return Locale.forLanguageTag(customLang);
        }

        // 2. Check Accept-Language header
        String acceptLang = request.getHeader("Accept-Language");
        if (acceptLang != null && !acceptLang.isEmpty()) {
            return Locale.LanguageRange.parse(acceptLang).stream()
                    .map(range -> Locale.forLanguageTag(range.getRange()))
                    .findFirst()
                    .orElse(Locale.getDefault());
        }

        // 3. (Optional) IP-based detection using GeoIP (Placeholder)
        // String ip = request.getRemoteAddr();
        // Locale ipLocale = geoIpService.getLocaleFromIp(ip);
        // if (ipLocale != null) return ipLocale;

        // 4. Default fallback
        return Locale.getDefault();
    }
}
