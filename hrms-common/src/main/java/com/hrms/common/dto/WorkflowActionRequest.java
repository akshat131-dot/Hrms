package com.hrms.common.dto;

import lombok.*;

@Getter
@Setter
public class WorkflowActionRequest {
    private Long instanceId;
    private String actor;
    private String remarks;
}
