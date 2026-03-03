package com.sportsant.saas.scheduler.service;

import com.sportsant.saas.scheduler.entity.JobLog;
import com.sportsant.saas.scheduler.entity.ScheduledJob;
import com.sportsant.saas.scheduler.repository.JobLogRepository;
import com.sportsant.saas.scheduler.repository.ScheduledJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskSchedulerService {

    @Autowired
    private ScheduledJobRepository jobRepository;

    @Autowired
    private JobLogRepository logRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private ThreadPoolTaskScheduler taskScheduler;
    private Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @PostConstruct
    public void init() {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("SportsAnt-Job-");
        taskScheduler.initialize();
        
        // Load enabled jobs
        List<ScheduledJob> jobs = jobRepository.findByEnabledTrue();
        for (ScheduledJob job : jobs) {
            scheduleJob(job);
        }
    }

    public void scheduleJob(ScheduledJob job) {
        // Cancel existing if any
        if (scheduledTasks.containsKey(job.getId())) {
            scheduledTasks.get(job.getId()).cancel(false);
        }

        Runnable task = () -> executeJob(job);
        try {
            ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(job.getCronExpression()));
            scheduledTasks.put(job.getId(), future);
            System.out.println("Scheduled job: " + job.getJobName() + " [" + job.getCronExpression() + "]");
        } catch (Exception e) {
            System.err.println("Failed to schedule job: " + job.getJobName() + " - " + e.getMessage());
        }
    }

    public void executeJob(ScheduledJob job) {
        JobLog log = new JobLog();
        log.setJobId(job.getId());
        log.setJobName(job.getJobName());
        logRepository.save(log);

        try {
            // Locate Bean and execute run()
            Object bean = applicationContext.getBean(job.getBeanName());
            if (bean instanceof Runnable) {
                ((Runnable) bean).run();
                log.setStatus("SUCCESS");
                log.setMessage("Executed successfully");
            } else {
                throw new RuntimeException("Bean " + job.getBeanName() + " is not Runnable");
            }
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            log.setEndTime(LocalDateTime.now());
            logRepository.save(log);
            
            // Only update lastRunAt if not triggered manually
            job.setLastRunAt(LocalDateTime.now());
            jobRepository.save(job);
        }
    }

    public void triggerJob(Long jobId) {
        ScheduledJob job = jobRepository.findById(jobId).orElseThrow();
        new Thread(() -> executeJob(job)).start();
    }

    public ScheduledJob createJob(ScheduledJob job) {
        ScheduledJob saved = jobRepository.save(job);
        if (saved.isEnabled()) {
            scheduleJob(saved);
        }
        return saved;
    }
    
    public void deleteJob(Long id) {
        if (scheduledTasks.containsKey(id)) {
            scheduledTasks.get(id).cancel(false);
            scheduledTasks.remove(id);
        }
        jobRepository.deleteById(id);
    }
    
    public List<ScheduledJob> getAllJobs() {
        return jobRepository.findAll();
    }
    
    public List<JobLog> getJobLogs(Long jobId) {
        return logRepository.findByJobIdOrderByStartTimeDesc(jobId);
    }
}
