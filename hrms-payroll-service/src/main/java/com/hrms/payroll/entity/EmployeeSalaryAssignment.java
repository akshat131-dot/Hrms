package com.hrms.payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_salary_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeSalaryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    private SalaryStructure salaryStructure;
}
