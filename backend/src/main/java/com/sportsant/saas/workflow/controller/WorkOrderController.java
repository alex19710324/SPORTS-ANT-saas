package com.sportsant.saas.workflow.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.workflow.entity.WorkOrder;
import com.sportsant.saas.workflow.entity.WorkOrderLog;
import com.sportsant.saas.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
@Tag(name = "工单中心", description = "设备维修、任务指派、进度追踪")
public class WorkOrderController {

    @Autowired
    private WorkflowService workOrderService;

    @GetMapping("/tickets")
    @Operation(summary = "Get All Tickets")
    public Result<List<WorkOrder>> getAllTickets() {
        return Result.success(workOrderService.getAllTickets());
    }

    @PostMapping("/tickets")
    @Operation(summary = "Create Ticket")
    public Result<WorkOrder> createTicket(@RequestBody WorkOrder ticket) {
        return Result.success(workOrderService.createWorkOrder(ticket));
    }

    @PutMapping("/tickets/{id}/status")
    @Operation(summary = "Update Ticket Status")
    public Result<WorkOrder> updateStatus(@PathVariable Long id, @RequestParam String status, @RequestParam String comment) {
        // Mock Operator ID
        String operatorId = "ADMIN-001";
        return Result.success(workOrderService.updateStatus(id, status, operatorId, comment));
    }

    @GetMapping("/tickets/{id}/logs")
    @Operation(summary = "Get Ticket Logs")
    public Result<List<WorkOrderLog>> getLogs(@PathVariable Long id) {
        return Result.success(workOrderService.getLogs(id));
    }
}
