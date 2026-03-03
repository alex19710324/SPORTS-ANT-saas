package com.sportsant.saas.system.aspect;

import com.sportsant.saas.system.service.SystemAuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private SystemAuditService auditService;

    // Pointcut for all Controllers (excluding GET requests usually)
    @Pointcut("execution(* com.sportsant.saas.*.controller.*.*(..)) && !execution(* com.sportsant.saas.*.controller.*.get*(..))")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String module = className.replace("Controller", "").toUpperCase();
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();
        
        // Mock User (In real app, get from SecurityContext)
        String userId = "USER-001"; 
        String username = "Admin";
        
        // Enhance details with result ID if possible
        String details = "Executed " + methodName;
        if (result != null) {
            try {
                // Try to get ID via reflection or just toString
                 details += " [Result: " + result.getClass().getSimpleName() + "]";
            } catch (Exception e) {
                // ignore
            }
        }
        
        auditService.logAction(userId, username, methodName.toUpperCase(), module, details, ip);
    }
}
