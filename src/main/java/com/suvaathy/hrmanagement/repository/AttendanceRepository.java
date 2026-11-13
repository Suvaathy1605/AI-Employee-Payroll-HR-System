package com.suvaathy.hrmanagement.repository;

import com.suvaathy.hrmanagement.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<Attendance> findByOrderByPeriodDesc();
    List<Attendance> findByEmployeeIdOrderByPeriodDesc(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
}