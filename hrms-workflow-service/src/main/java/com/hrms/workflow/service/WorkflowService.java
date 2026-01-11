package com.hrms.workflow.service;

import com.hrms.common.dto.*;
import com.hrms.workflow.entity.*;
import com.hrms.workflow.enums.*;
import com.hrms.workflow.repository.*;
import com.hrms.common.event.WorkflowApprovedEvent;
import com.hrms.common.event.WorkflowRejectedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkflowService {

    private final WorkflowDefinitionRepository defRepo;
    private final WorkflowInstanceRepository instRepo;
    private final WorkflowHistoryRepository histRepo;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    public WorkflowService(
            WorkflowDefinitionRepository defRepo,
            WorkflowInstanceRepository instRepo,
            WorkflowHistoryRepository histRepo,
            NotificationService notificationService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.defRepo = defRepo;
        this.instRepo = instRepo;
        this.histRepo = histRepo;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public WorkflowInstance start(WorkflowStartRequest req) {

        WorkflowDefinition def = defRepo.findByModule(req.getModule())
                .orElseThrow(() ->
                        new RuntimeException("No workflow definition found for module: " + req.getModule())
                );

        WorkflowInstance i = new WorkflowInstance();
        i.setWorkflow(def);
        i.setReferenceId(req.getReferenceId());
        i.setCurrentStep(1);
        i.setStatus(WorkflowStatus.PENDING);

        return instRepo.save(i);
    }

    @Transactional
    public void approve(WorkflowActionRequest req) {

        WorkflowInstance i = instRepo.findById(req.getInstanceId())
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        record(i, req.getActor(), ApprovalAction.APPROVE, req.getRemarks());

        if (i.getCurrentStep() >= i.getWorkflow().getTotalSteps()) {

            i.setStatus(WorkflowStatus.COMPLETED);
            instRepo.save(i);
            eventPublisher.publishEvent(
                    new WorkflowApprovedEvent(
                            i.getWorkflow().getModule(),
                            i.getReferenceId(),
                            req.getActor(),     // STRING
                            req.getRemarks()
                    )
            );

            notificationService.notifyEvent("WORKFLOW_COMPLETED", req.getActor());

        } else {
            i.setCurrentStep(i.getCurrentStep() + 1);
            instRepo.save(i);
        }
    }

    @Transactional
    public void reject(WorkflowActionRequest req) {

        WorkflowInstance i = instRepo.findById(req.getInstanceId())
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        i.setStatus(WorkflowStatus.REJECTED);
        instRepo.save(i);

        record(i, req.getActor(), ApprovalAction.REJECT, req.getRemarks());


        eventPublisher.publishEvent(
                new WorkflowRejectedEvent(
                        i.getWorkflow().getModule(),
                        i.getReferenceId(),
                        req.getActor(),   // STRING
                        req.getRemarks()
                )
        );

        notificationService.notifyEvent("WORKFLOW_REJECTED", req.getActor());
    }

    public List<WorkflowHistory> history(Long id) {
        return histRepo.findAll().stream()
                .filter(h -> h.getInstanceId().equals(id))
                .toList();
    }

    private void record(WorkflowInstance i, String actor, ApprovalAction a, String r) {
        WorkflowHistory h = new WorkflowHistory();
        h.setInstanceId(i.getId());
        h.setActor(actor);
        h.setAction(a);
        h.setRemarks(r);
        h.setActionTime(LocalDateTime.now());
        histRepo.save(h);
    }
}
