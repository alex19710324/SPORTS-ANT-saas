package com.sportsant.saas.crm.controller;

import com.sportsant.saas.crm.entity.Lead;
import com.sportsant.saas.crm.service.CRMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm")
@Tag(name = "客户关系管理 (CRM)", description = "线索管理、转化、跟进")
public class CRMController {

    @Autowired
    private CRMService crmService;

    @GetMapping("/leads")
    @Operation(summary = "Get all leads", description = "获取所有销售线索")
    public List<Lead> getLeads() {
        return crmService.getAllLeads();
    }

    @PostMapping("/leads")
    @Operation(summary = "Create lead", description = "创建新线索 (自动评分)")
    public Lead createLead(@RequestBody Lead lead) {
        return crmService.createLead(lead);
    }

    @PostMapping("/leads/{id}/convert")
    @Operation(summary = "Convert lead", description = "将线索转化为会员")
    public void convertLead(@PathVariable Long id) {
        crmService.convertLeadToMember(id);
    }

    @PutMapping("/leads/{id}/status")
    @Operation(summary = "Update status", description = "更新跟进状态")
    public Lead updateStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String notes) {
        return crmService.updateLeadStatus(id, status, notes);
    }
}
