package com.sportsant.saas.device.repository;

import com.sportsant.saas.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findBySerialNumber(String serialNumber);
    List<Device> findByStatus(String status);
    List<Device> findByLocation(String location);
}
