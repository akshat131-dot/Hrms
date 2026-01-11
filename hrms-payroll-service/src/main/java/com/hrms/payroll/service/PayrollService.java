package com.hrms.payroll.service;

import com.hrms.attendence.service.AttendanceService;
import com.hrms.auth.entity.User;
import com.hrms.common.dto.AttendancePayrollSummary;
import com.hrms.employee.repository.EmployeeRepository;
import com.hrms.payroll.dto.PayslipDto;
import com.hrms.payroll.entity.*;
import com.hrms.payroll.exception.ResourceNotFoundException;
import com.hrms.payroll.repository.EmployeeSalaryAssignmentRepository;
import com.hrms.payroll.repository.PayrollRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollService {

    private final EmployeeSalaryAssignmentRepository assignRepo;
    private final PayrollRunRepository runRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendanceService attendanceService;

    @Value("${payroll.pf.percent:12}")
    private BigDecimal pfPercent;

    @Value("${payroll.tds.percent:5}")
    private BigDecimal tdsPercent;

    @Value("${payroll.pt.fixed:200}")
    private BigDecimal ptFixed;

    @Value("${payroll.esi.threshold:21000}")
    private BigDecimal esiThreshold;

    @Value("${payroll.esi.percent:0.75}")
    private BigDecimal esiPercent;

    private static final int SCALE = 2;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    private boolean hasRole(User user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private void validatePayslipAccess(Long empId) {
        User user = currentUser();

        if (hasRole(user, "ADMIN") || hasRole(user, "HR")) {
            return;
        }

        if (!hasRole(user, "EMPLOYEE")) {
            throw new AccessDeniedException("Unauthorized role");
        }

        if (!Objects.equals(user.getEmployeeId(), empId)) {
            throw new AccessDeniedException("Employees can access only their own payslip");
        }
    }

    public PayslipDto runPayrollForMonth(Long empId, int month, int year) {

        User user = currentUser();
        if (!(hasRole(user, "ADMIN") || hasRole(user, "HR"))) {
            throw new AccessDeniedException("Only Admin or HR can run payroll");
        }

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        if (!employeeRepo.existsById(empId)) {
            throw new ResourceNotFoundException("Employee not found: " + empId);
        }

        EmployeeSalaryAssignment assignment = assignRepo.findByEmployeeId(empId);

        if (assignment == null) {
            throw new ResourceNotFoundException(
                    "No salary assignment for employee: " + empId
            );
        }

        PayrollRun existingRun =
                runRepo.findByEmployeeIdAndMonthAndYear(empId, month, year)
                        .orElse(null);

        if (existingRun != null && existingRun.isLocked()) {
            throw new IllegalStateException(
                    "Payroll is locked for this employee and month"
            );
        }

        AttendancePayrollSummary attendance =
                attendanceService.getAttendanceForPayroll(
                        empId,
                        year,
                        Month.of(month)
                );

        if (attendance.getWorkingDays() == 0) {
            throw new IllegalStateException(
                    "No working days found for payroll calculation"
            );
        }

        SalaryStructure structure = assignment.getSalaryStructure();

        BigDecimal monthlyCtc =
                structure.getAnnualCtc()
                        .divide(BigDecimal.valueOf(12), SCALE, RoundingMode.HALF_UP);

        BigDecimal perDaySalary =
                monthlyCtc.divide(
                        BigDecimal.valueOf(attendance.getWorkingDays()),
                        SCALE,
                        RoundingMode.HALF_UP
                );

        BigDecimal payableGross =
                perDaySalary
                        .multiply(BigDecimal.valueOf(attendance.getPresentDays()))
                        .add(
                                perDaySalary
                                        .multiply(BigDecimal.valueOf(attendance.getHalfDays()))
                                        .multiply(BigDecimal.valueOf(0.5))
                        )
                        .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal basic =
                payableGross.multiply(BigDecimal.valueOf(0.40))
                        .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal gross = payableGross;

        BigDecimal pf =
                basic.multiply(pfPercent)
                        .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP);

        BigDecimal esi = BigDecimal.ZERO;
        if (gross.compareTo(esiThreshold) < 0) {
            esi = gross.multiply(esiPercent)
                    .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal pt = ptFixed.setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal tds =
                gross.multiply(tdsPercent)
                        .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP);

        BigDecimal deductions =
                pf.add(esi).add(pt).add(tds)
                        .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal net =
                gross.subtract(deductions)
                        .setScale(SCALE, RoundingMode.HALF_UP);

        PayrollRun run =
                existingRun != null
                        ? existingRun
                        : PayrollRun.builder()
                        .employeeId(empId)
                        .month(month)
                        .year(year)
                        .build();

        run.setGrossSalary(gross);
        run.setNetSalary(net);
        run.setPf(pf);
        run.setEsi(esi);
        run.setPt(pt);
        run.setTds(tds);

        return toPayslip(runRepo.save(run));
    }

    public PayslipDto getPayslip(Long empId, int month, int year) {

        validatePayslipAccess(empId);

        PayrollRun run =
                runRepo.findByEmployeeIdAndMonthAndYear(empId, month, year)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Payslip not found")
                        );

        return toPayslip(run);
    }

    public PayslipDto getPayslipForCurrentEmployee(int month, int year) {

        User user = currentUser();

        if (!hasRole(user, "EMPLOYEE")) {
            throw new AccessDeniedException("Only employees can access this endpoint");
        }

        if (user.getEmployeeId() == null) {
            throw new ResourceNotFoundException("Employee not linked with user");
        }

        return getPayslip(user.getEmployeeId(), month, year);
    }

    public void lockPayroll(Long empId, int month, int year) {

        User user = currentUser();
        if (!(hasRole(user, "ADMIN") || hasRole(user, "HR"))) {
            throw new AccessDeniedException("Only Admin or HR can lock payroll");
        }

        PayrollRun run =
                runRepo.findByEmployeeIdAndMonthAndYear(empId, month, year)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Payroll not found")
                        );

        run.setLocked(true);
        runRepo.save(run);
    }

    private PayslipDto toPayslip(PayrollRun run) {
        return PayslipDto.builder()
                .employeeId(run.getEmployeeId())
                .month(run.getMonth())
                .year(run.getYear())
                .grossSalary(run.getGrossSalary())
                .netSalary(run.getNetSalary())
                .pf(run.getPf())
                .esi(run.getEsi())
                .pt(run.getPt())
                .tds(run.getTds())
                .generatedAt(run.getCreatedAt())
                .build();
    }
}
