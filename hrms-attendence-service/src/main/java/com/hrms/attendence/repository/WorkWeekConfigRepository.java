package com.hrms.attendence.repository;

import com.hrms.attendence.entity.WorkWeekConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkWeekConfigRepository
        extends JpaRepository<WorkWeekConfig, String> {

    boolean existsByDayOfWeekAndWorkingTrue(String dayOfWeek);
}
