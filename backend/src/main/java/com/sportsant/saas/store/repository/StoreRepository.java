package com.sportsant.saas.store.repository;

import com.sportsant.saas.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByCountry(String country);
    List<Store> findByStatus(String status);
}
