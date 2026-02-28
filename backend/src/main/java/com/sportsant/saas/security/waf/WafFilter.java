package com.sportsant.saas.security.waf;

import com.sportsant.saas.ai.service.AiAware;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A lightweight Web Application Firewall (WAF) filter.
 * Detects and blocks SQL Injection and XSS attacks.
 */
@Component
public class WafFilter implements Filter, AiAware {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Basic patterns for SQLi and XSS
    private static final Pattern SQL_INJECTION = Pattern.compile("(?i)('|--|;|\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|OR)\\b)");
    private static final Pattern XSS = Pattern.compile("(?i)(<script>|javascript:|on\\w+=)");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        
        // Inspect query parameters
        String queryString = req.getQueryString();
        if (queryString != null && isMalicious(queryString)) {
            blockRequest(req, (HttpServletResponse) response, "Malicious Query Param");
            return;
        }

        // Note: For body inspection, we'd need a caching wrapper, omitted for brevity.
        
        chain.doFilter(request, response);
    }

    private boolean isMalicious(String input) {
        return SQL_INJECTION.matcher(input).find() || XSS.matcher(input).find();
    }

    private void blockRequest(HttpServletRequest req, HttpServletResponse res, String reason) throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.getWriter().write("Blocked by WAF: " + reason);
        
        // Notify AI
        eventPublisher.publishEvent(createEvent("WAF_BLOCK", Map.of(
            "ip", req.getRemoteAddr(),
            "uri", req.getRequestURI(),
            "reason", reason
        )));
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // e.g. Update WAF rules dynamically
    }
}
