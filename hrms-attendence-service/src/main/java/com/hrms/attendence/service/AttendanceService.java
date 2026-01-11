package com.hrms.attendence.service;

import com.hrms.attendence.dto.*;
import com.hrms.attendence.entity.AttendanceLog;
import com.hrms.attendence.entity.AttendanceStatus;
import com.hrms.attendence.repository.AttendanceLogRepository;
import com.hrms.auth.entity.User;
import com.hrms.common.dto.AttendancePayrollSummary;
import com.hrms.common.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final int STANDARD_WORK_MINUTES = 8 * 60;

    private final AttendanceLogRepository attendanceLogRepository;
    private final HolidayService holidayService;
    private final WorkWeekService workWeekService;

    public AttendanceService(
            AttendanceLogRepository attendanceLogRepository,
            HolidayService holidayService,
            WorkWeekService workWeekService
    ) {
        this.attendanceLogRepository = attendanceLogRepository;
        this.holidayService = holidayService;
        this.workWeekService = workWeekService;
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    private boolean hasRole(User user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private void validateEmployeeAccess(Long employeeId) {
        User user = currentUser();

        if (hasRole(user, "ADMIN") || hasRole(user, "HR")) {
            return;
        }

        if (!hasRole(user, "EMPLOYEE")) {
            throw new AccessDeniedException("Unauthorized");
        }

        if (!Objects.equals(user.getEmployeeId(), employeeId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    @Transactional
    public PunchResponse punch(PunchType punchType) {

        User user = currentUser();

        if (!hasRole(user, "EMPLOYEE")) {
            throw new AccessDeniedException("Only employees can punch");
        }

        Long employeeId = user.getEmployeeId();
        if (employeeId == null) {
            throw new BusinessException("EMPLOYEE_NOT_LINKED", "Employee not linked");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        AttendanceLog log = attendanceLogRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseGet(() -> {
                    AttendanceLog l = new AttendanceLog();
                    l.setEmployeeId(employeeId);
                    l.setAttendanceDate(today);
                    l.setStatus(AttendanceStatus.PENDING_APPROVAL);
                    return l;
                });

        if (punchType == PunchType.IN) {
            if (log.getPunchIn() != null) {
                throw new BusinessException("ALREADY_PUNCHED_IN", "Already punched in");
            }
            log.setPunchIn(now);
        } else {
            if (log.getPunchIn() == null) {
                throw new BusinessException("NO_PUNCH_IN", "Punch in first");
            }
            if (log.getPunchOut() != null) {
                throw new BusinessException("ALREADY_PUNCHED_OUT", "Already punched out");
            }

            log.setPunchOut(now);
            int worked = (int) Duration.between(log.getPunchIn(), now).toMinutes();
            log.setWorkedMinutes(worked);
            log.setOvertimeMinutes(Math.max(worked - STANDARD_WORK_MINUTES, 0));
        }

        log.setStatus(AttendanceStatus.PENDING_APPROVAL);
        attendanceLogRepository.save(log);

        return toPunchResponse(log);
    }

    public DailyAttendanceResponse getDailyAttendance(Long employeeId, LocalDate date) {

        validateEmployeeAccess(employeeId);

        AttendanceLog log = attendanceLogRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, date)
                .orElse(null);

        DailyAttendanceResponse res = new DailyAttendanceResponse();
        res.setEmployeeId(employeeId);
        res.setDate(date);

        if (log == null) {
            res.setStatus(AttendanceStatus.ABSENT.name());
            res.setWorkedMinutes(0);
            res.setOvertimeMinutes(0);
            return res;
        }

        res.setPunchIn(log.getPunchIn());
        res.setPunchOut(log.getPunchOut());
        res.setWorkedMinutes(Optional.ofNullable(log.getWorkedMinutes()).orElse(0));
        res.setOvertimeMinutes(Optional.ofNullable(log.getOvertimeMinutes()).orElse(0));
        res.setStatus(log.getStatus().name());
        return res;
    }

    public MonthlyAttendanceResponse getMonthlyAttendance(
            Long employeeId,
            int year,
            Month month
    ) {

        validateEmployeeAccess(employeeId);

        MonthlyAttendanceCombinedResponse combined =
                getMonthlyAttendanceCombined(employeeId, year, month);

        MonthlyAttendanceResponse res = new MonthlyAttendanceResponse();
        res.setEmployeeId(employeeId);
        res.setYear(year);
        res.setMonth(month.name());
        res.setPresentDays(combined.getPresentDays());
        res.setAbsentDays(combined.getAbsentDays());
        res.setOvertimeMinutes(combined.getOvertimeMinutes());

        return res;
    }

    public MonthlyAttendanceCombinedResponse getMonthlyAttendanceCombined(
            Long employeeId,
            int year,
            Month month
    ) {

        validateEmployeeAccess(employeeId);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());

        List<AttendanceLog> logs =
                attendanceLogRepository.findByEmployeeIdAndAttendanceDateBetween(
                        employeeId, start, end
                );

        Map<LocalDate, AttendanceLog> logMap =
                logs.stream().collect(Collectors.toMap(
                        AttendanceLog::getAttendanceDate,
                        l -> l,
                        (a, b) -> a
                ));

        int workingDays = 0;
        int presentDays = 0;
        int overtimeMinutes = 0;

        List<MonthlyDayAttendance> days = new ArrayList<>();

        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {

            if (holidayService.isHoliday(cursor)
                    || !workWeekService.isWorkingDay(cursor.getDayOfWeek())) {
                cursor = cursor.plusDays(1);
                continue;
            }

            workingDays++;

            MonthlyDayAttendance day = new MonthlyDayAttendance();
            day.setDate(cursor);

            AttendanceLog log = logMap.get(cursor);

            if (log != null && log.getStatus() != AttendanceStatus.REJECTED) {
                presentDays++;
                overtimeMinutes += Optional.ofNullable(log.getOvertimeMinutes()).orElse(0);
                day.setStatus(log.getStatus().name());
                day.setWorkedMinutes(Optional.ofNullable(log.getWorkedMinutes()).orElse(0));
                day.setOvertimeMinutes(Optional.ofNullable(log.getOvertimeMinutes()).orElse(0));
            } else {
                day.setStatus(AttendanceStatus.ABSENT.name());
                day.setWorkedMinutes(0);
                day.setOvertimeMinutes(0);
            }

            days.add(day);
            cursor = cursor.plusDays(1);
        }

        MonthlyAttendanceCombinedResponse res = new MonthlyAttendanceCombinedResponse();
        res.setEmployeeId(employeeId);
        res.setYear(year);
        res.setMonth(month.name());
        res.setWorkingDays(workingDays);
        res.setPresentDays(presentDays);
        res.setAbsentDays(workingDays - presentDays);
        res.setOvertimeMinutes(overtimeMinutes);
        res.setDays(days);

        return res;
    }

    public AttendancePayrollSummary getAttendanceForPayroll(
            Long employeeId,
            int year,
            Month month
    ) {

        MonthlyAttendanceCombinedResponse combined =
                getMonthlyAttendanceCombined(employeeId, year, month);

        AttendancePayrollSummary summary = new AttendancePayrollSummary();
        summary.setEmployeeId(employeeId);
        summary.setYear(year);
        summary.setMonth(month.name());
        summary.setWorkingDays(combined.getWorkingDays());
        summary.setPresentDays(combined.getPresentDays());
        summary.setAbsentDays(combined.getAbsentDays());
        summary.setHalfDays(0);
        summary.setOvertimeMinutes(combined.getOvertimeMinutes());

        return summary;
    }

    @Transactional
    public void approveAttendance(Long logId) {

        User user = currentUser();
        if (!(hasRole(user, "ADMIN") || hasRole(user, "HR"))) {
            throw new AccessDeniedException("Forbidden");
        }

        AttendanceLog log = attendanceLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Not found"));

        log.setStatus(AttendanceStatus.APPROVED);
        attendanceLogRepository.save(log);
    }

    @Transactional
    public void rejectAttendance(Long logId) {

        User user = currentUser();
        if (!(hasRole(user, "ADMIN") || hasRole(user, "HR"))) {
            throw new AccessDeniedException("Forbidden");
        }

        AttendanceLog log = attendanceLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Not found"));

        log.setStatus(AttendanceStatus.REJECTED);
        attendanceLogRepository.save(log);
    }

    private PunchResponse toPunchResponse(AttendanceLog log) {
        PunchResponse r = new PunchResponse();
        r.setEmployeeId(log.getEmployeeId());
        r.setAttendanceDate(log.getAttendanceDate());
        r.setPunchIn(log.getPunchIn());
        r.setPunchOut(log.getPunchOut());
        r.setWorkedMinutes(Optional.ofNullable(log.getWorkedMinutes()).orElse(0));
        r.setOvertimeMinutes(Optional.ofNullable(log.getOvertimeMinutes()).orElse(0));
        r.setStatus(log.getStatus().name());
        return r;
    }
}
