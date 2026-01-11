package com.hrms.payroll.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "salary_structures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "annual_ctc", nullable = false, precision = 19, scale = 2)
    private BigDecimal annualCtc;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "structure_components",
        joinColumns = @JoinColumn(name = "structure_id"),
        inverseJoinColumns = @JoinColumn(name = "component_id")
    )
    private List<SalaryComponent> components;
}
