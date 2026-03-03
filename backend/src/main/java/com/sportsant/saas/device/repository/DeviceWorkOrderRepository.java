package com.sportsant.saas.device.repository;

import com.sportsant.saas.device.entity.DeviceWorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceWorkOrderRepository extends JpaRepository<DeviceWorkOrder, Long> {
    List<DeviceWorkOrder> findByDeviceId(Long deviceId);
}
