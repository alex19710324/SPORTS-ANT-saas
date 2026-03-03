package com.sportsant.saas.common.context;

import java.util.Locale;
import java.time.ZoneId;

public class UserContext {
    private static final ThreadLocal<UserContextData> context = new ThreadLocal<>();

    public static void set(Long userId, Long tenantId, String locale, String timezone) {
        context.set(new UserContextData(userId, tenantId, locale, timezone));
    }

    public static void clear() {
        context.remove();
    }

    public static Long getUserId() {
        return context.get() != null ? context.get().userId : null;
    }

    public static Long getTenantId() {
        return context.get() != null ? context.get().tenantId : null;
    }

    public static String getLocale() {
        return context.get() != null ? context.get().locale : Locale.US.toLanguageTag();
    }
    
    public static ZoneId getTimezone() {
        if (context.get() != null && context.get().timezone != null) {
            try {
                return ZoneId.of(context.get().timezone);
            } catch (Exception e) {
                // ignore
            }
        }
        return ZoneId.of("UTC");
    }

    private static class UserContextData {
        Long userId;
        Long tenantId;
        String locale;
        String timezone;

        public UserContextData(Long userId, Long tenantId, String locale, String timezone) {
            this.userId = userId;
            this.tenantId = tenantId;
            this.locale = locale;
            this.timezone = timezone;
        }
    }
}
