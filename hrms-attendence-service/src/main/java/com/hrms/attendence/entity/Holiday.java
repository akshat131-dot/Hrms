package com.hrms.attendence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "holiday_calendar")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate holidayDate;

    private String name;

    // true = working holiday, false = non-working
    private boolean workingDay;

    // getters & setters
}

