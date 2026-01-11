package com.hrms.common.dto;


import lombok.*;

@Getter @Setter
public class WorkflowStartRequest {
    private String module;
    private Long referenceId;
}

