package com.hrms.leave.repository;

import com.hrms.leave.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    LeaveBalance findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    List<LeaveBalance> findByEmployeeId(Long employeeId);
}

