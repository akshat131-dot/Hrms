package com.hrms.common.dto;

import lombok.Data;

@Data

public class AttendancePayrollSummary {
    private Long employeeId;
    private int year;
    private String month;
    private int workingDays;
    private int presentDays;
    private int halfDays;
    private int absentDays;
    private int overtimeMinutes;
}


