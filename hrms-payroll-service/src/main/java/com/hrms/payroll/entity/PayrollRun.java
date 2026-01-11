package com.hrms.payroll.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payroll_runs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
        },
        indexes = {
                @Index(columnList = "employee_id, month, year")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(precision = 19, scale = 2)
    private BigDecimal grossSalary;

    @Column(precision = 19, scale = 2)
    private BigDecimal netSalary;

    @Column(precision = 19, scale = 2)
    private BigDecimal pf;

    @Column(precision = 19, scale = 2)
    private BigDecimal esi;

    @Column(precision = 19, scale = 2)
    private BigDecimal pt;

    @Column(precision = 19, scale = 2)
    private BigDecimal tds;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
