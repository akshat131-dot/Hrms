
package com.hrms.workflow.controller;

import com.hrms.common.dto.*;
import com.hrms.workflow.entity.WorkflowHistory;
import com.hrms.workflow.entity.WorkflowInstance;
import com.hrms.workflow.service.WorkflowService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

 private final WorkflowService service;

 public WorkflowController(WorkflowService service) {
     this.service = service;
 }

 @PostMapping("/start")
 public WorkflowInstance start(@RequestBody WorkflowStartRequest r) {
     return service.start(r);
 }

 @PostMapping("/approve")
 public String approve(@RequestBody WorkflowActionRequest r) {
     service.approve(r);
     return "APPROVED";
 }

 @PostMapping("/reject")
 public String reject(@RequestBody WorkflowActionRequest r) {
     service.reject(r);
     return "REJECTED";
 }

 @GetMapping("/history/{id}")
 public List<WorkflowHistory> history(@PathVariable Long id) {
     return service.history(id);
 }
}
