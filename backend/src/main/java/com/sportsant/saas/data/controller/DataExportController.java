package com.sportsant.saas.data.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.data.entity.DataExportTask;
import com.sportsant.saas.data.service.DataExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data")
@Tag(name = "Data Export", description = "Data Export and Import Management")
public class DataExportController {

    @Autowired
    private DataExportService dataService;

    @PostMapping("/export")
    @Operation(summary = "Create Export Task")
    public Result<DataExportTask> createExportTask(@RequestParam String name, @RequestParam String type) {
        // Mock User ID = 1
        Long userId = 1L;
        DataExportTask task = dataService.createTask(name, type, userId);
        dataService.processTask(task.getId()); // Trigger async processing
        return Result.success(task);
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get My Export Tasks")
    public Result<List<DataExportTask>> getMyTasks() {
        Long userId = 1L;
        return Result.success(dataService.getUserTasks(userId));
    }
    
    // In real app, add a download endpoint that streams the file
}
