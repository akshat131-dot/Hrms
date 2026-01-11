
package com.hrms.workflow.repository;
import com.hrms.workflow.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {}
