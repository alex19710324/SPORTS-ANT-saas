package com.sportsant.saas.hr.repository;

import com.sportsant.saas.hr.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByStoreId(Long storeId);
}
