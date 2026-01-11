package com.hrms.payroll.service;

import com.hrms.payroll.dto.CreateStructureDto;
import com.hrms.payroll.entity.SalaryComponent;
import com.hrms.payroll.entity.SalaryStructure;
import com.hrms.payroll.exception.ResourceNotFoundException;
import com.hrms.payroll.repository.SalaryComponentRepository;
import com.hrms.payroll.repository.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureService {

    private final SalaryStructureRepository structureRepo;
    private final SalaryComponentRepository componentRepo;

    public SalaryStructure create(CreateStructureDto dto) {

        List<SalaryComponent> components = dto.getComponentIds().stream()
                .map(id -> componentRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Component " + id + " not found")))
                .toList();


        BigDecimal basic = components.stream()
                .filter(c -> c.getCode().equalsIgnoreCase("BASIC"))
                .map(SalaryComponent::getFixedAmount)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("BASIC salary component is required for structure calculation"));

        BigDecimal monthlyEarnings = BigDecimal.ZERO;
        BigDecimal monthlyDeductions = BigDecimal.ZERO;


        for (SalaryComponent c : components) {
            if (c.getFixedAmount() != null) {
                if (c.getType() == SalaryComponent.ComponentType.EARNING) {
                    monthlyEarnings = monthlyEarnings.add(c.getFixedAmount());
                } else {
                    monthlyDeductions = monthlyDeductions.add(c.getFixedAmount());
                }
            }


            if (c.getPercentage() != null) {
                BigDecimal value = basic.multiply(c.getPercentage())
                        .divide(BigDecimal.valueOf(100));

                if (c.getType() == SalaryComponent.ComponentType.EARNING) {
                    monthlyEarnings = monthlyEarnings.add(value);
                } else {
                    monthlyDeductions = monthlyDeductions.add(value);
                }
            }
        }


        BigDecimal annualCtc = monthlyEarnings.multiply(BigDecimal.valueOf(12));

        SalaryStructure structure = SalaryStructure.builder()
                .name(dto.getName())
                .components(components)
                .annualCtc(annualCtc)
                .build();

        return structureRepo.save(structure);
    }
}
