package com.sportsant.saas.lcms.repository;

import com.sportsant.saas.lcms.entity.LcmsProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LcmsProjectRepository extends JpaRepository<LcmsProject, Long> {
    Optional<LcmsProject> findByCode(String code);
}
