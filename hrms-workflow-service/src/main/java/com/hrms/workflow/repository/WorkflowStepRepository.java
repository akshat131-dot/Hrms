
package com.hrms.workflow.repository;
import com.hrms.workflow.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {}
