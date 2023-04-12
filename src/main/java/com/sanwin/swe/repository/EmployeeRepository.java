package com.sanwin.swe.repository;

import com.sanwin.swe.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Page<Employee> findBySalaryBetween(Double minSalary, Double maxSalary, Pageable pageable);
    Page<Employee> findBySalaryGreaterThanEqual(Double salary, Pageable pageable);
    Page<Employee> findBySalaryLessThanEqual(Double salary, Pageable pageable);
}
