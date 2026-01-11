
package com.hrms.workflow.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.hrms.workflow.enums.*;

@Entity @Getter @Setter
@Table(name="workflow_steps")
public class WorkflowStep {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 private int stepOrder;
 private String approverRole;
 @ManyToOne private WorkflowDefinition workflow;
}

