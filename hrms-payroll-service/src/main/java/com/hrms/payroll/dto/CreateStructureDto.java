package com.hrms.payroll.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStructureDto {
    @NotBlank private String name;
    private BigDecimal annualCtc;
    @NotEmpty private List<Long> componentIds;
}
