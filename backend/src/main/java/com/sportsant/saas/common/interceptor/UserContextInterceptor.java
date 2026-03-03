package com.sportsant.saas.common.interceptor;

import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.globalization.service.TimezoneService;
import com.sportsant.saas.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Autowired
    private TimezoneService timezoneService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = null;
        Long tenantId = null;
        String locale = request.getHeader("X-Locale");
        if (locale == null || locale.isEmpty()) {
            locale = request.getLocale().toLanguageTag();
        }
        
        String timezone = request.getHeader("X-Timezone");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            userId = userDetails.getId();
            // Assuming tenantId is in UserDetails, for now default to null or from DB
            
            // If user is logged in, their profile settings override request headers
            if (timezone == null || timezone.isEmpty()) {
                timezone = timezoneService.getUserTimezone(userId).getId();
            }
        }

        UserContext.set(userId, tenantId, locale, timezone);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
