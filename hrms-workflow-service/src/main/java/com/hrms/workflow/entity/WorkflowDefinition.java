package com.hrms.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "workflow_definition",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "module")
        }
)
public class WorkflowDefinition {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, length = 50)
 private String module;

 @Column(nullable = false)
 private int totalSteps;
}
