package com.hrms.leave.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;  // PENDING, APPROVED, REJECTED
    private String reason;
    private LocalDate appliedOn;
}
