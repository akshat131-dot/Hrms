package com.hrms.attendence.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PunchRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private PunchType punchType;

    private LocalDateTime punchTime;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public PunchType getPunchType() {
        return punchType;
    }

    public void setPunchType(PunchType punchType) {
        this.punchType = punchType;
    }

    public LocalDateTime getPunchTime() {
        return punchTime;
    }

    public void setPunchTime(LocalDateTime punchTime) {
        this.punchTime = punchTime;
    }
}
