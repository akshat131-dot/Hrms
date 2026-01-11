
package com.hrms.workflow.service;

import com.hrms.workflow.entity.NotificationLog;
import com.hrms.workflow.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class NotificationService {

 private final NotificationLogRepository logRepo;

 public NotificationService(NotificationLogRepository logRepo) {
     this.logRepo = logRepo;
 }

 public void notifyEvent(String event, String recipient) {
     NotificationLog log = new NotificationLog();
     log.setEvent(event);
     log.setRecipient(recipient);
     log.setSentAt(LocalDateTime.now());
     logRepo.save(log);
 }
}
