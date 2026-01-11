package com.hrms.employee.repository;

import com.hrms.employee.entity.EmployeeContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeContactRepository extends JpaRepository<EmployeeContact, Long> {
}
