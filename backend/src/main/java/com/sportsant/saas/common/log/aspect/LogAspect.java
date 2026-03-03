package com.sportsant.saas.common.log.aspect;

import com.sportsant.saas.audit.entity.OperationAuditLog;
import com.sportsant.saas.audit.service.OperationAuditService;
import com.sportsant.saas.common.log.annotation.Log;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationAuditService auditService;

    @Around("@annotation(com.sportsant.saas.common.log.annotation.Log)")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        
        String resultStr = "Success";
        try {
            return point.proceed();
        } catch (Throwable e) {
            resultStr = "Failed: " + e.getMessage();
            throw e;
        } finally {
            long time = System.currentTimeMillis() - beginTime;
            saveLog(point, time, resultStr);
        }
    }

    private void saveLog(ProceedingJoinPoint point, long time, String resultStr) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        OperationAuditLog auditLog = new OperationAuditLog();
        
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            auditLog.setOperation(logAnnotation.value());
        }
        
        // Request Info
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            auditLog.setIp(request.getRemoteAddr());
            auditLog.setMethod(request.getMethod());
            // Mock Username for now
            auditLog.setUsername("admin"); // In real app: SecurityContextHolder.getContext().getAuthentication().getName()
        }
        
        auditLog.setParams(Arrays.toString(point.getArgs()));
        auditLog.setExecutionTime(time);
        auditLog.setResult(resultStr);
        
        auditService.saveLog(auditLog);
    }
}
