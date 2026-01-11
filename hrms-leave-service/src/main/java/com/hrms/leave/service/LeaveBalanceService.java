package com.hrms.leave.service;

import com.hrms.leave.entity.LeaveBalance;
import com.hrms.leave.entity.LeaveType;
import com.hrms.leave.repository.LeaveBalanceRepository;
import com.hrms.leave.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveBalanceService {

    private final LeaveBalanceRepository repo;
    private final LeaveTypeRepository typeRepo;

    public LeaveBalance assign(Long empId, Long typeId, int bal) {

        if (bal < 0) {
            throw new RuntimeException("Leave balance cannot be negative");
        }

        LeaveType type = typeRepo.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Invalid leave type"));


        LeaveBalance existing = repo.findByEmployeeIdAndLeaveTypeId(empId, typeId);

        if (existing != null) {

            existing.setBalance(bal);
            return repo.save(existing);
        }
        LeaveBalance lb = new LeaveBalance();
        lb.setEmployeeId(empId);
        lb.setLeaveType(type);
        lb.setBalance(bal);

        return repo.save(lb);
    }

    public List<LeaveBalance> getAll(Long empId) {
        return repo.findByEmployeeId(empId);
    }
}
