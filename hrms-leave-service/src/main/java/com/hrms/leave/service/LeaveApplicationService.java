package com.hrms.leave.service;

import com.hrms.leave.dto.LeaveApplyRequest;
import com.hrms.leave.entity.LeaveBalance;
import com.hrms.leave.entity.LeaveRequest;
import com.hrms.leave.entity.LeaveType;
import com.hrms.leave.repository.LeaveBalanceRepository;
import com.hrms.leave.repository.LeaveRequestRepository;
import com.hrms.leave.repository.LeaveTypeRepository;
import com.hrms.common.dto.WorkflowStartRequest;
import com.hrms.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveTypeRepository typeRepo;
    private final LeaveBalanceRepository balanceRepo;
    private final LeaveRequestRepository requestRepo;
    private final WorkflowService workflowService;

    public LeaveRequest apply(LeaveApplyRequest dto) {

        LeaveType type = typeRepo.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid leave type"));

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        LeaveBalance balance = balanceRepo.findByEmployeeIdAndLeaveTypeId(
                dto.getEmployeeId(),
                type.getId()
        );

        if (balance == null) {
            throw new RuntimeException("Leave balance not set");
        }

        long days = ChronoUnit.DAYS.between(
                dto.getStartDate(),
                dto.getEndDate()
        ) + 1;

        if (days <= 0) {
            throw new RuntimeException("Invalid number of leave days");
        }

        if (balance.getBalance() < days) {
            throw new RuntimeException("Insufficient leave balance");
        }

        balance.setBalance(balance.getBalance() - (int) days);
        balanceRepo.save(balance);

        LeaveRequest req = new LeaveRequest();
        req.setEmployeeId(dto.getEmployeeId());
        req.setLeaveType(type);
        req.setStartDate(dto.getStartDate());
        req.setEndDate(dto.getEndDate());
        req.setReason(dto.getReason());
        req.setStatus("PENDING");
        req.setAppliedOn(LocalDate.now());

        LeaveRequest savedRequest = requestRepo.save(req);


        WorkflowStartRequest wfReq = new WorkflowStartRequest();
        wfReq.setModule("LEAVE");
        wfReq.setReferenceId(savedRequest.getId());

        workflowService.start(wfReq);

        return savedRequest;
    }
}
