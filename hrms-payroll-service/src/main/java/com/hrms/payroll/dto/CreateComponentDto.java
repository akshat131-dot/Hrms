package com.hrms.payroll.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.hrms.payroll.entity.SalaryComponent.ComponentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateComponentDto {
    @NotBlank private String code;
    @NotBlank private String name;
    @NotNull private ComponentType type;
    private BigDecimal percentage;
    private BigDecimal fixedAmount;
}
