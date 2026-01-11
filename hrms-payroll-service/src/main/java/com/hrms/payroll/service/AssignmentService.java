package com.hrms.payroll.service;

import com.hrms.payroll.entity.EmployeeSalaryAssignment;
import com.hrms.payroll.entity.SalaryStructure;
import com.hrms.payroll.exception.ResourceNotFoundException;
import com.hrms.payroll.repository.EmployeeSalaryAssignmentRepository;
import com.hrms.payroll.repository.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final EmployeeSalaryAssignmentRepository assignRepo;
    private final SalaryStructureRepository structureRepo;

    public EmployeeSalaryAssignment assign(Long empId, Long structureId) {
        SalaryStructure s = structureRepo.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure not found"));

        EmployeeSalaryAssignment existing = assignRepo.findByEmployeeId(empId);
        if (existing != null) {
            existing.setSalaryStructure(s);
            return assignRepo.save(existing);
        }

        EmployeeSalaryAssignment a = EmployeeSalaryAssignment.builder()
                .employeeId(empId)
                .salaryStructure(s)
                .build();
        return assignRepo.save(a);
    }
}
