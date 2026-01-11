package com.hrms.leave.repository;

import com.hrms.leave.entity.LeaveApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveApprovalRepository extends JpaRepository<LeaveApproval, Long> {

}
