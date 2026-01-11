package com.hrms.attendence.service;

import com.hrms.attendence.repository.WorkWeekConfigRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;

@Service
public class WorkWeekService {

    private final WorkWeekConfigRepository repository;

    public WorkWeekService(WorkWeekConfigRepository repository) {
        this.repository = repository;
    }


    public boolean isWorkingDay(DayOfWeek day) {


        if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
            return true;
        }
        return repository.existsByDayOfWeekAndWorkingTrue(day.name());
    }
}
