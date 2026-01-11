package com.hrms.leave.controller;

import com.hrms.common.dto.WorkflowActionRequest;
import com.hrms.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave/approval")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final WorkflowService workflowService;

    @PostMapping("/approve")
    public String approve(
            @RequestParam Long workflowInstanceId,
            @RequestParam Long managerId,
            @RequestParam(required = false) String remarks
    ) {
        WorkflowActionRequest req = new WorkflowActionRequest();
        req.setInstanceId(workflowInstanceId);
        req.setActor(managerId.toString());
        req.setRemarks(remarks);

        workflowService.approve(req);
        return "Leave approved successfully";
    }

    @PostMapping("/reject")
    public String reject(
            @RequestParam Long workflowInstanceId,
            @RequestParam Long managerId,
            @RequestParam(required = false) String remarks
    ) {
        WorkflowActionRequest req = new WorkflowActionRequest();
        req.setInstanceId(workflowInstanceId);
        req.setActor(managerId.toString());
        req.setRemarks(remarks);

        workflowService.reject(req);
        return "Leave rejected successfully";
    }
}
