package com.hrms.payroll.service;

import com.hrms.payroll.dto.CreateComponentDto;
import com.hrms.payroll.entity.SalaryComponent;
import com.hrms.payroll.exception.BadRequestException;
import com.hrms.payroll.repository.SalaryComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryComponentService {

    private final SalaryComponentRepository repo;

    public SalaryComponent create(CreateComponentDto dto) {
        if (dto.getPercentage() == null && dto.getFixedAmount() == null) {
            throw new BadRequestException("Either percentage or fixedAmount must be provided");
        }
        if (repo.findByCode(dto.getCode()).isPresent()) {
            throw new BadRequestException("Component code already exists");
        }
        SalaryComponent c = SalaryComponent.builder()
                .code(dto.getCode().toUpperCase().trim())
                .name(dto.getName())
                .type(dto.getType())
                .percentage(dto.getPercentage())
                .fixedAmount(dto.getFixedAmount())
                .build();
        return repo.save(c);
    }
}
