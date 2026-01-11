package com.hrms.leave.service;

import com.hrms.leave.entity.LeaveApproval;
import com.hrms.leave.entity.LeaveRequest;
import com.hrms.leave.repository.LeaveApprovalRepository;
import com.hrms.leave.repository.LeaveRequestRepository;
import com.hrms.common.event.WorkflowApprovedEvent;
import com.hrms.common.event.WorkflowRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveApprovalService {

    private final LeaveRequestRepository requestRepo;
    private final LeaveApprovalRepository approvalRepo;

    @EventListener
    public void onWorkflowApproved(WorkflowApprovedEvent event) {

        if (!"LEAVE".equals(event.getModule())) return;

        processDecision(
                event.getReferenceId(),
                "APPROVED",
                event.getRemarks()
        );
    }

    @EventListener
    public void onWorkflowRejected(WorkflowRejectedEvent event) {

        if (!"LEAVE".equals(event.getModule())) return;

        processDecision(
                event.getReferenceId(),
                "REJECTED",
                event.getRemarks()
        );
    }

    private void processDecision(
            Long requestId,
            String action,
            String remarks
    ) {

        LeaveRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Leave request not in PENDING state");
        }

        req.setStatus(action);
        requestRepo.save(req);

        LeaveApproval approval = new LeaveApproval();
        approval.setRequestId(requestId);
        approval.setAction(action);
        approval.setRemarks(remarks);
        approval.setActionDate(LocalDate.now());

        approvalRepo.save(approval);
    }
}
