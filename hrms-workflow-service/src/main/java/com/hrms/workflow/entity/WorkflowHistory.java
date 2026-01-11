
package com.hrms.workflow.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.hrms.workflow.enums.*;

@Entity @Getter @Setter
@Table(name="workflow_history")
public class WorkflowHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 private Long instanceId;
 private String actor;
 @Enumerated(EnumType.STRING)
 private ApprovalAction action;
 private String remarks;
 private LocalDateTime actionTime;
}

