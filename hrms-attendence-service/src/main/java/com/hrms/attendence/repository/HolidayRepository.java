package com.hrms.attendence.repository;

import com.hrms.attendence.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByHolidayDateAndWorkingDayFalse(LocalDate holidayDate);
}

