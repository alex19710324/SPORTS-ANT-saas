package com.sportsant.saas.membership.repository;

import com.sportsant.saas.membership.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);
    Optional<Member> findByMemberCode(String memberCode);
    Optional<Member> findByPhoneNumber(String phoneNumber);
    java.util.List<Member> findByStatus(String status);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.joinDate BETWEEN :start AND :end")
    Integer countMembersJoinedBetween(LocalDateTime start, LocalDateTime end);
}
