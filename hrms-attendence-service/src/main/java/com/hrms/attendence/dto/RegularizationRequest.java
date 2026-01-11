package com.hrms.attendence.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RegularizationRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate date;

    @NotNull
    private String reason;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
