package com.sportsant.saas.audit.controller;

import com.sportsant.saas.audit.entity.OperationAuditLog;
import com.sportsant.saas.audit.service.OperationAuditService;
import com.sportsant.saas.common.log.annotation.Log;
import com.sportsant.saas.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "审计日志", description = "系统操作日志、安全审计")
public class AuditController {

    @Autowired
    private OperationAuditService auditService;

    @GetMapping("/logs")
    @Operation(summary = "Get Recent Logs")
    @Log("View Audit Logs")
    public Result<List<OperationAuditLog>> getLogs() {
        return Result.success(auditService.getRecentLogs());
    }
}
