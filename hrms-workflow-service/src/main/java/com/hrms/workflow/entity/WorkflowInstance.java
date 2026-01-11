
package com.hrms.workflow.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.hrms.workflow.enums.*;

@Entity @Getter @Setter
@Table(name="workflow_instances")
public class WorkflowInstance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 private Long referenceId;
 private int currentStep;
 @Enumerated(EnumType.STRING)
 private WorkflowStatus status;
 @ManyToOne private WorkflowDefinition workflow;
}

