package com.sportsant.saas.maintenance.service;

import com.sportsant.saas.maintenance.entity.Device;
import com.sportsant.saas.maintenance.entity.MaintenanceTask;
import com.sportsant.saas.maintenance.repository.DeviceRepository;
import com.sportsant.saas.maintenance.repository.MaintenanceTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MaintenanceTaskRepository maintenanceTaskRepository;

    public List<MaintenanceTask> getPendingTasks() {
        return maintenanceTaskRepository.findByStatusNot("COMPLETED");
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Scheduled(cron = "0 0 1 * * ?") // Daily at 1 AM
    @Transactional
    public void generatePreventiveTasks() {
        List<Device> devices = deviceRepository.findAll();
        for (Device device : devices) {
            if (device.getMaintenanceIntervalDays() != null) {
                // Simplified Logic: If no recent task, create one
                // Real logic would check last completed task date
                MaintenanceTask task = new MaintenanceTask();
                task.setDevice(device);
                task.setDescription("Preventive Maintenance: " + device.getName());
                task.setDueDate(LocalDate.now().plusDays(7));
                task.setStatus("PENDING");
                maintenanceTaskRepository.save(task);
            }
        }
    }

    @Transactional
    public void completeTask(Long taskId) {
        MaintenanceTask task = maintenanceTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus("COMPLETED");
        task.setCompletedDate(LocalDate.now());
        maintenanceTaskRepository.save(task);
    }
}
