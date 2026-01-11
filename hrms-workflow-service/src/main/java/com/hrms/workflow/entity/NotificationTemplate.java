
package com.hrms.workflow.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.hrms.workflow.enums.*;

@Entity @Getter @Setter
@Table(name="notification_templates")
public class NotificationTemplate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 private String event;
 private String message;
}

