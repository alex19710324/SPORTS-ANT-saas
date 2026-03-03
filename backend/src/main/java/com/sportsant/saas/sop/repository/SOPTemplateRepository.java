package com.sportsant.saas.sop.repository;

import com.sportsant.saas.sop.entity.SOPTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SOPTemplateRepository extends JpaRepository<SOPTemplate, Long> {
    List<SOPTemplate> findByTargetRole(String targetRole);
}
