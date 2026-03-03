package com.sportsant.saas.social.service;

import com.sportsant.saas.social.entity.SocialAccount;
import com.sportsant.saas.social.entity.SocialContent;
import com.sportsant.saas.social.repository.SocialAccountRepository;
import com.sportsant.saas.social.repository.SocialContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class SocialService {

    @Autowired
    private SocialAccountRepository accountRepository;

    @Autowired
    private SocialContentRepository contentRepository;

    public List<SocialAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    public SocialAccount addAccount(SocialAccount account) {
        account.setStatus("ACTIVE");
        account.setLastSyncTime(LocalDateTime.now());
        // Mock Initial Sync
        account.setFollowersCount(new Random().nextInt(5000));
        account.setEngagementRate(new Random().nextInt(1000)); // 10.00%
        return accountRepository.save(account);
    }

    public SocialContent createContent(SocialContent content) {
        if (content.getScheduledTime() == null) {
            content.setStatus("PUBLISHED"); // Publish Immediately
            content.setPublishedTime(LocalDateTime.now());
            publishToPlatforms(content);
        } else {
            content.setStatus("SCHEDULED");
        }
        return contentRepository.save(content);
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void processScheduledPosts() {
        List<SocialContent> scheduled = contentRepository.findByStatusAndScheduledTimeBefore("SCHEDULED", LocalDateTime.now());
        for (SocialContent content : scheduled) {
            publishToPlatforms(content);
            content.setStatus("PUBLISHED");
            content.setPublishedTime(LocalDateTime.now());
            contentRepository.save(content);
            System.out.println("Auto-Published Scheduled Content: " + content.getTitle());
        }
    }

    // Simulate API calls to WeCom, Douyin, etc.
    private void publishToPlatforms(SocialContent content) {
        String[] platforms = content.getPlatforms().split(",");
        for (String platform : platforms) {
            System.out.println("Publishing [" + content.getTitle() + "] to Platform: " + platform.trim());
            // In real life: call platform SDK
        }
    }
    
    // Simulate Data Sync (Followers, Likes)
    @Scheduled(cron = "0 0 * * * ?") // Hourly
    public void syncAccountStats() {
        List<SocialAccount> accounts = accountRepository.findByStatus("ACTIVE");
        Random rand = new Random();
        for (SocialAccount acc : accounts) {
            acc.setFollowersCount(acc.getFollowersCount() + rand.nextInt(10)); // Organic growth
            acc.setLastSyncTime(LocalDateTime.now());
            accountRepository.save(acc);
        }
    }
}
