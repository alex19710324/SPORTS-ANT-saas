package com.sportsant.saas.membership.repository;

import com.sportsant.saas.membership.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);
    Optional<Member> findByMemberCode(String memberCode);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}
