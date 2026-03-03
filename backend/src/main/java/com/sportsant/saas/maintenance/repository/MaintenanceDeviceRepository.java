package com.sportsant.saas.maintenance.repository;

import com.sportsant.saas.maintenance.entity.MaintenanceDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceDeviceRepository extends JpaRepository<MaintenanceDevice, Long> {
    List<MaintenanceDevice> findByStatus(String status);
}
