package com.sportsant.saas.security;

import com.sportsant.saas.tenant.entity.Tenant;
import com.sportsant.saas.tenant.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AppKeyFilter extends OncePerRequestFilter {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Only check /api/open/** requests
        if (!request.getRequestURI().startsWith("/api/open")) {
            filterChain.doFilter(request, response);
            return;
        }

        String appKey = request.getHeader("X-App-Key");
        if (appKey != null && !appKey.isEmpty()) {
            Tenant tenant = tenantRepository.findByAppKey(appKey).orElse(null);

            if (tenant != null && "ACTIVE".equals(tenant.getStatus())) {
                // Create a special authentication object for the merchant
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        tenant, null, Collections.emptyList());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
