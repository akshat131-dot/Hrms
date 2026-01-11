package com.hrms.attendence.service;

import com.hrms.attendence.dto.RegularizationRequest;
import com.hrms.attendence.dto.RegularizationResponse;
import com.hrms.attendence.entity.AttendanceRegularization;
import com.hrms.attendence.entity.RegularizationStatus;
import com.hrms.attendence.repository.AttendanceRegularizationRepository;
import com.hrms.common.dto.WorkflowStartRequest;
import com.hrms.workflow.service.WorkflowService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AttendanceRegularizationService {

    private final AttendanceRegularizationRepository regularizationRepository;
    private final WorkflowService workflowService;

    public AttendanceRegularizationService(
            AttendanceRegularizationRepository regularizationRepository,
            WorkflowService workflowService
    ) {
        this.regularizationRepository = regularizationRepository;
        this.workflowService = workflowService;
    }

    @Transactional
    public RegularizationResponse createRegularization(RegularizationRequest request) {

        AttendanceRegularization reg = new AttendanceRegularization();
        reg.setEmployeeId(request.getEmployeeId());
        reg.setAttendanceDate(request.getDate());
        reg.setReason(request.getReason());
        reg.setStatus(RegularizationStatus.PENDING);

        AttendanceRegularization saved = regularizationRepository.save(reg);

        WorkflowStartRequest wfReq = new WorkflowStartRequest();
        wfReq.setModule("ATTENDANCE_REGULARIZATION");
        wfReq.setReferenceId(saved.getId());

        workflowService.start(wfReq);

        RegularizationResponse resp = new RegularizationResponse();
        resp.setId(saved.getId());
        resp.setEmployeeId(saved.getEmployeeId());
        resp.setDate(saved.getAttendanceDate());
        resp.setReason(saved.getReason());
        resp.setStatus(saved.getStatus().name());
        resp.setRequestedAt(saved.getRequestedAt());

        return resp;
    }
}
