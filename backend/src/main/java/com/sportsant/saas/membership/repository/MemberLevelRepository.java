package com.sportsant.saas.membership.repository;

import com.sportsant.saas.membership.entity.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberLevelRepository extends JpaRepository<MemberLevel, Long> {
    Optional<MemberLevel> findByLevelOrder(Integer levelOrder);
}
