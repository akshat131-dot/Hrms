package com.hrms.payroll.controller;

import com.hrms.common.response.ApiResponse;
import com.hrms.payroll.dto.CreateComponentDto;
import com.hrms.payroll.dto.CreateStructureDto;
import com.hrms.payroll.dto.PayslipDto;
import com.hrms.payroll.entity.*;
import com.hrms.payroll.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
@Validated
public class PayrollController {

    private final SalaryComponentService componentService;
    private final SalaryStructureService structureService;
    private final AssignmentService assignmentService;
    private final PayrollService payrollService;

    @PostMapping("/component")
    public ResponseEntity<ApiResponse<SalaryComponent>> createComponent(
            @Valid @RequestBody CreateComponentDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Salary component created successfully",
                        componentService.create(dto)
                )
        );
    }

    @PostMapping("/structure")
    public ResponseEntity<ApiResponse<SalaryStructure>> createStructure(
            @Valid @RequestBody CreateStructureDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Salary structure created successfully",
                        structureService.create(dto)
                )
        );
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<EmployeeSalaryAssignment>> assignStructure(
            @RequestParam Long empId,
            @RequestParam Long structureId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Salary structure assigned successfully",
                        assignmentService.assign(empId, structureId)
                )
        );
    }

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<PayslipDto>> runPayroll(
            @RequestParam Long empId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payroll generated successfully",
                        payrollService.runPayrollForMonth(empId, month, year)
                )
        );
    }

    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<String>> lockPayroll(
            @RequestParam Long empId,
            @RequestParam int month,
            @RequestParam int year) {

        payrollService.lockPayroll(empId, month, year);

        return ResponseEntity.ok(
                ApiResponse.success("Payroll locked successfully", "LOCKED")
        );
    }

    @GetMapping("/me/payslip")
    public ResponseEntity<ApiResponse<PayslipDto>> myPayslip(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "My payslip",
                        payrollService.getPayslipForCurrentEmployee(month, year)
                )
        );
    }

    @GetMapping("/{empId}/payslip")
    public ResponseEntity<ApiResponse<PayslipDto>> payslipByEmployee(
            @PathVariable Long empId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payslip fetched successfully",
                        payrollService.getPayslip(empId, month, year)
                )
        );
    }
}
