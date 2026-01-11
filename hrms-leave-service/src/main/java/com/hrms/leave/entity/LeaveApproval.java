package com.hrms.leave.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestId;
    private Long managerId;
    private String action; // APPROVED, REJECTED
    private String remarks;
    private LocalDate actionDate;
}
