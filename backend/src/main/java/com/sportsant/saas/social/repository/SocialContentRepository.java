package com.sportsant.saas.social.repository;

import com.sportsant.saas.social.entity.SocialContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SocialContentRepository extends JpaRepository<SocialContent, Long> {
    List<SocialContent> findByStatus(String status);
    List<SocialContent> findByStatusAndScheduledTimeBefore(String status, LocalDateTime time);
}
