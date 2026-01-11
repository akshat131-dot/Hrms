package com.hrms.leave.controller;

import com.hrms.leave.dto.LeaveApplyRequest;
import com.hrms.leave.entity.LeaveBalance;
import com.hrms.leave.entity.LeaveRequest;
import com.hrms.leave.service.LeaveApplicationService;
import com.hrms.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveApplicationService appService;
    private final LeaveBalanceService balanceService;

    @PostMapping("/apply")
    public LeaveRequest apply(@RequestBody LeaveApplyRequest dto) {
        return appService.apply(dto);
    }

    @PostMapping("/assign-balance")
    public LeaveBalance assign(@RequestParam Long empId, @RequestParam Long typeId, @RequestParam int balance) {
        return balanceService.assign(empId, typeId, balance);
    }

    @GetMapping("/balances/{empId}")
    public List<LeaveBalance> getAll(@PathVariable Long empId) {
        return balanceService.getAll(empId);
    }
}
