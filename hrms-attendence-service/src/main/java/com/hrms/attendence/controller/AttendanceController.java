package com.hrms.attendence.controller;

import com.hrms.attendence.dto.*;
import com.hrms.attendence.service.AttendanceRegularizationService;
import com.hrms.attendence.service.AttendanceService;
import com.hrms.auth.entity.User;
import com.hrms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceRegularizationService regularizationService;

    public AttendanceController(
            AttendanceService attendanceService,
            AttendanceRegularizationService regularizationService
    ) {
        this.attendanceService = attendanceService;
        this.regularizationService = regularizationService;
    }

    private Long currentEmployeeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getEmployeeId();
    }

    @PostMapping("/punch-in")
    public ResponseEntity<ApiResponse<PunchResponse>> punchIn() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Punch-in successful",
                        attendanceService.punch(PunchType.IN)
                )
        );
    }

    @PostMapping("/punch-out")
    public ResponseEntity<ApiResponse<PunchResponse>> punchOut() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Punch-out successful",
                        attendanceService.punch(PunchType.OUT)
                )
        );
    }

    @PostMapping("/regularize")
    public ResponseEntity<ApiResponse<RegularizationResponse>> regularize(
            @RequestBody RegularizationRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Regularization request created",
                        regularizationService.createRegularization(request)
                )
        );
    }


    @GetMapping("/me/daily")
    public ResponseEntity<ApiResponse<DailyAttendanceResponse>> myDailyAttendance(
            @RequestParam(required = false) String date) {

        LocalDate d = (date == null || date.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(date);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "My daily attendance",
                        attendanceService.getDailyAttendance(currentEmployeeId(), d)
                )
        );
    }

    @GetMapping("/me/month")
    public ResponseEntity<ApiResponse<MonthlyAttendanceResponse>> myMonthlyAttendance(
            @RequestParam String month,
            @RequestParam int year) {

        Month m = Month.valueOf(month.toUpperCase(Locale.ROOT));

        return ResponseEntity.ok(
                ApiResponse.success(
                        "My monthly attendance",
                        attendanceService.getMonthlyAttendance(
                                currentEmployeeId(), year, m
                        )
                )
        );
    }

    @GetMapping("/me/month/combined")
    public ResponseEntity<ApiResponse<MonthlyAttendanceCombinedResponse>> myMonthlyCombinedAttendance(
            @RequestParam String month,
            @RequestParam int year) {

        Month m = Month.valueOf(month.toUpperCase(Locale.ROOT));

        return ResponseEntity.ok(
                ApiResponse.success(
                        "My monthly combined attendance",
                        attendanceService.getMonthlyAttendanceCombined(
                                currentEmployeeId(), year, m
                        )
                )
        );
    }



    @GetMapping("/{empId}/daily")
    public ResponseEntity<ApiResponse<DailyAttendanceResponse>> dailyByEmployee(
            @PathVariable Long empId,
            @RequestParam String date) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Daily attendance",
                        attendanceService.getDailyAttendance(empId, LocalDate.parse(date))
                )
        );
    }

    @GetMapping("/{empId}/month")
    public ResponseEntity<ApiResponse<MonthlyAttendanceResponse>> monthlyByEmployee(
            @PathVariable Long empId,
            @RequestParam String month,
            @RequestParam int year) {

        Month m = Month.valueOf(month.toUpperCase(Locale.ROOT));

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Monthly attendance",
                        attendanceService.getMonthlyAttendance(empId, year, m)
                )
        );
    }

    @GetMapping("/{empId}/month/combined")
    public ResponseEntity<ApiResponse<MonthlyAttendanceCombinedResponse>> monthlyCombinedByEmployee(
            @PathVariable Long empId,
            @RequestParam String month,
            @RequestParam int year) {

        Month m = Month.valueOf(month.toUpperCase(Locale.ROOT));

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Monthly combined attendance",
                        attendanceService.getMonthlyAttendanceCombined(empId, year, m)
                )
        );
    }

    @PostMapping("/approve/{logId}")
    public ResponseEntity<ApiResponse<String>> approveAttendance(
            @PathVariable Long logId) {

        attendanceService.approveAttendance(logId);

        return ResponseEntity.ok(
                ApiResponse.success("Attendance approved", "APPROVED")
        );
    }

    @PostMapping("/reject/{logId}")
    public ResponseEntity<ApiResponse<String>> rejectAttendance(
            @PathVariable Long logId) {

        attendanceService.rejectAttendance(logId);

        return ResponseEntity.ok(
                ApiResponse.success("Attendance rejected", "REJECTED")
        );
    }
}
