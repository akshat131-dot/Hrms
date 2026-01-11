package com.hrms.attendence.service;

import com.hrms.attendence.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDateAndWorkingDayFalse(date);
    }
}
