package com.hrms.common.event;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkflowRejectedEvent {

    private final String module;
    private final Long referenceId;
    private final String actor;
    private final String remarks;
}

