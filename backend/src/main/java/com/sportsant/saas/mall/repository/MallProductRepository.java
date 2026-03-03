package com.sportsant.saas.mall.repository;

import com.sportsant.saas.mall.entity.MallProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MallProductRepository extends JpaRepository<MallProduct, Long> {
    List<MallProduct> findByEnabledTrue();
    List<MallProduct> findByType(String type);
}
