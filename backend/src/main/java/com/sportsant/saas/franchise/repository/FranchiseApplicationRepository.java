package com.sportsant.saas.franchise.repository;

import com.sportsant.saas.franchise.entity.FranchiseApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FranchiseApplicationRepository extends JpaRepository<FranchiseApplication, Long> {
    List<FranchiseApplication> findByStatus(String status);
}
