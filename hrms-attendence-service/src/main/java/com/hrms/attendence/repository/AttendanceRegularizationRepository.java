package com.hrms.attendence.repository;

import com.hrms.attendence.entity.AttendanceRegularization;
import com.hrms.attendence.entity.RegularizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRegularizationRepository extends JpaRepository<AttendanceRegularization, Long> {

    List<AttendanceRegularization> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    List<AttendanceRegularization> findByStatus(RegularizationStatus status);
}
