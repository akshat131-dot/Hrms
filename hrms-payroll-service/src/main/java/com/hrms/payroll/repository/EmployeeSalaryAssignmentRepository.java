package com.hrms.payroll.repository;

import com.hrms.payroll.entity.EmployeeSalaryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeSalaryAssignmentRepository extends JpaRepository<EmployeeSalaryAssignment, Long> {
    EmployeeSalaryAssignment findByEmployeeId(Long employeeId);
}
