package com.sportsant.saas.scheduler.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.scheduler.entity.JobLog;
import com.sportsant.saas.scheduler.entity.ScheduledJob;
import com.sportsant.saas.scheduler.service.TaskSchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler")
@Tag(name = "任务调度", description = "定时任务管理、日志查看")
public class SchedulerController {

    @Autowired
    private TaskSchedulerService schedulerService;

    @GetMapping("/jobs")
    @Operation(summary = "Get all jobs", description = "Retrieve a list of all scheduled jobs")
    public Result<List<ScheduledJob>> getJobs() {
        return Result.success(schedulerService.getAllJobs());
    }

    @PostMapping("/jobs")
    @Operation(summary = "Create a job", description = "Create a new scheduled job configuration")
    public Result<ScheduledJob> createJob(@RequestBody ScheduledJob job) {
        return Result.success(schedulerService.createJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @Operation(summary = "Delete a job", description = "Delete a scheduled job by ID")
    public Result<?> deleteJob(@PathVariable Long id) {
        schedulerService.deleteJob(id);
        return Result.success();
    }

    @PostMapping("/jobs/{id}/trigger")
    @Operation(summary = "Trigger a job", description = "Manually trigger a job immediately")
    public Result<?> triggerJob(@PathVariable Long id) {
        schedulerService.triggerJob(id);
        return Result.success();
    }

    @GetMapping("/jobs/{id}/logs")
    @Operation(summary = "Get job logs", description = "Get execution logs for a specific job")
    public Result<List<JobLog>> getLogs(@PathVariable Long id) {
        return Result.success(schedulerService.getJobLogs(id));
    }
}
