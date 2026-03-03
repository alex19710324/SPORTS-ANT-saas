package com.sportsant.saas.data.service;

import com.sportsant.saas.data.entity.DataExportTask;
import com.sportsant.saas.data.repository.DataExportTaskRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataExportService {

    @Autowired
    private DataExportTaskRepository taskRepository;

    @Autowired
    private MemberRepository memberRepository;

    public DataExportTask createTask(String name, String type, Long userId) {
        DataExportTask task = new DataExportTask();
        task.setTaskName(name);
        task.setType(type);
        task.setCreatedBy(userId);
        return taskRepository.save(task);
    }

    @Async
    public void processTask(Long taskId) {
        DataExportTask task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return;

        task.setStatus("PROCESSING");
        taskRepository.save(task);

        try {
            File file = generateFile(task.getType());
            // In real world: Upload to S3/OSS and get URL
            // For MVP: Return local file path or mock URL
            String fileUrl = "/api/data/download/" + file.getName();
            
            task.setStatus("COMPLETED");
            task.setDownloadUrl(fileUrl);
            task.setCompletedAt(LocalDateTime.now());
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setErrorMsg(e.getMessage());
            e.printStackTrace();
        } finally {
            taskRepository.save(task);
        }
    }

    private File generateFile(String type) throws IOException {
        String fileName = type + "_" + System.currentTimeMillis() + ".csv";
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);
        
        try (FileWriter writer = new FileWriter(file)) {
            if ("MEMBER".equals(type)) {
                writer.write("ID,Name,Phone,Points,Balance\n");
                List<Member> members = memberRepository.findAll();
                for (Member m : members) {
                    writer.write(String.format("%d,%s,%s,%d,%.2f\n", 
                        m.getId(), m.getName(), m.getPhoneNumber(), m.getPoints(), m.getBalance()));
                }
            } else {
                writer.write("Unknown Type\n");
            }
        }
        return file;
    }

    public List<DataExportTask> getUserTasks(Long userId) {
        return taskRepository.findByCreatedByOrderByCreatedAtDesc(userId);
    }
}
