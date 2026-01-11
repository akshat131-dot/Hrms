package com.hrms.payroll.repository;

import com.hrms.payroll.entity.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    Optional<PayrollRun> findByEmployeeIdAndMonthAndYear(Long empId, int month, int year);
}
