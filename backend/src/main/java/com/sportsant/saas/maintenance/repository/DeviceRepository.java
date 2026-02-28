package com.sportsant.saas.maintenance.repository;

import com.sportsant.saas.maintenance.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByStatus(String status);
}
