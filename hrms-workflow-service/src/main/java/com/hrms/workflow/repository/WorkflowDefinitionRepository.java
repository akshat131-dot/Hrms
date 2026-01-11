
package com.hrms.workflow.repository;
import java.util.Optional;
import com.hrms.workflow.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
    Optional<WorkflowDefinition> findByModule(String module);
}
