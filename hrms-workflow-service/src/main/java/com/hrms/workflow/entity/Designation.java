
package com.hrms.workflow.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.hrms.workflow.enums.*;

@Entity @Getter @Setter
@Table(name="designations")
public class Designation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 private String name;
}

