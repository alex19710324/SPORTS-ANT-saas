package com.sportsant.saas.mall.repository;

import com.sportsant.saas.mall.entity.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface MallOrderRepository extends JpaRepository<MallOrder, Long> {
    List<MallOrder> findByMemberId(String memberId);
    List<MallOrder> findByStatus(String status);
    Optional<MallOrder> findByOrderNo(String orderNo);

    @Query("SELECT SUM(o.cashPaid) FROM MallOrder o WHERE o.status = 'COMPLETED'")
    BigDecimal sumTotalRevenue();

    @Query("SELECT COUNT(o) FROM MallOrder o WHERE o.createdAt BETWEEN :start AND :end")
    Integer countOrdersBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(o.cashPaid) FROM MallOrder o WHERE o.status = 'COMPLETED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumRevenueBetween(LocalDateTime start, LocalDateTime end);

    List<MallOrder> findByProductIdAndCreatedAtBetweenAndStatus(
        Long productId, LocalDateTime start, LocalDateTime end, String status);
}
