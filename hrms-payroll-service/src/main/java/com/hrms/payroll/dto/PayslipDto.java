package com.hrms.payroll.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipDto {
    private Long employeeId;
    private int month;
    private int year;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private BigDecimal pf;
    private BigDecimal esi;
    private BigDecimal pt;
    private BigDecimal tds;
    private LocalDateTime generatedAt;
}
