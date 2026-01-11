package com.hrms.attendence.repository;

import com.hrms.attendence.entity.AttendanceLog;
import com.hrms.attendence.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {


    Optional<AttendanceLog> findByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );


    List<AttendanceLog> findByEmployeeIdAndAttendanceDateBetween(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );
    List<AttendanceLog> findByEmployeeIdAndStatusAndAttendanceDateBetween(
            Long employeeId,
            AttendanceStatus status,
            LocalDate startDate,
            LocalDate endDate
    );
}
