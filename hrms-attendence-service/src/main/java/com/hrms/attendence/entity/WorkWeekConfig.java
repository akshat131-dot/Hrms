package com.hrms.attendence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_week_config")
public class WorkWeekConfig {

    @Id
    private String dayOfWeek;

    private boolean working;

    // getters & setters
}