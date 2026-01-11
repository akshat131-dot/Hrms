package com.hrms.attendence.workflow;


import com.hrms.attendence.entity.AttendanceLog;
import com.hrms.attendence.entity.AttendanceStatus;
import com.hrms.attendence.repository.AttendanceLogRepository;
import com.hrms.common.event.WorkflowApprovedEvent;
import com.hrms.common.event.WorkflowRejectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AttendanceWorkflowListener {

    private final AttendanceLogRepository attendanceLogRepository;


    public AttendanceWorkflowListener(AttendanceLogRepository attendanceLogRepository) {
        this.attendanceLogRepository = attendanceLogRepository;
    }

    @EventListener
    public void onWorkflowApproved(WorkflowApprovedEvent event) {

        if (!"ATTENDANCE".equals(event.getModule())) {
            return;
        }

        AttendanceLog log = attendanceLogRepository.findById(event.getReferenceId())
                .orElseThrow(() ->
                        new RuntimeException("Attendance log not found"));

        if (log.getWorkedMinutes() >= 480) {
            log.setStatus(AttendanceStatus.PRESENT);
        } else if (log.getWorkedMinutes() >= 240) {
            log.setStatus(AttendanceStatus.HALF_DAY);
        } else {
            log.setStatus(AttendanceStatus.ABSENT);
        }

        attendanceLogRepository.save(log);
    }

    @EventListener
    public void onWorkflowRejected(WorkflowRejectedEvent event) {

        if (!"ATTENDANCE".equals(event.getModule())) {
            return;
        }

        AttendanceLog log = attendanceLogRepository.findById(event.getReferenceId())
                .orElseThrow(() ->
                        new RuntimeException("Attendance log not found"));

        log.setStatus(AttendanceStatus.ABSENT);
        attendanceLogRepository.save(log);
    }
}
